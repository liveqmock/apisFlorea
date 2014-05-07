package com.openIdeas.apps.apisflorea.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openIdeas.apps.apisflorea.entity.MailEntity;
import com.openIdeas.apps.apisflorea.entity.SmsOpLog;
import com.openIdeas.apps.apisflorea.enums.HandlerStatus;
import com.openIdeas.apps.apisflorea.exception.BizException;
import com.openIdeas.apps.apisflorea.intf.AnthenServiceIntf;
import com.openIdeas.apps.apisflorea.intf.MailMessageServiceIntf;
import com.openIdeas.apps.apisflorea.intf.OperatorLogServiceIntf;
import com.openIdeas.apps.apisflorea.intf.RequestHandlerIntf;
import com.openIdeas.apps.apisflorea.model.ReceiveSmsMsg;
import com.openIdeas.apps.apisflorea.result.CollectionResult;
import com.openIdeas.apps.apisflorea.result.GeniResult;
import com.openIdeas.apps.apisflorea.result.Result;

@Service(RequestHandlerIntf.SEND_SMS)
public class SmsServiceImpl extends AbstractRequestHandleImpl {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AnthenServiceIntf anthenService;

	@Autowired
	private OperatorLogServiceIntf operLogService;

	@Autowired
	private MailMessageServiceIntf mailMessageService;

	@Override
	public Result clientLogin() {
		Result r = new Result();
		// 短信接口认证登录
		if (!anthenService.isAnthed()) {
			logger.debug("{} 短信接口登录", "initParams");
			anthenService.anthenMsg(new ReceiveSmsMsg());
		}
		r.setSuccess(anthenService.isAnthed());
		return r;
	}

	protected String getForwardContent(Map<String, String> params) {
		// 1. 请求中获取内容
		String content = params.get("content");
		// content = "测试短信" + new Timestamp(System.currentTimeMillis());
		logger.debug("短信内容： {}", content);
		return content;
	}

	@Override
	protected GeniResult<HandlerStatus> handleForward(String msgId) {
		//初始化返回值
		GeniResult<HandlerStatus> result = new GeniResult<HandlerStatus>(
				HandlerStatus.N);
		String methodName = "handleForward";
		logger.debug("{}, 开始转发消息 msgId:{}", new Object[] { methodName, msgId });
		// 1. 先初始化队列
		CollectionResult<List<SmsOpLog>> colResult = operLogService
				.initOplogs(msgId);
		if (!colResult.isSuccess()) {
			throw new BizException(colResult);
		}

		// 2. 待发送邮件内容
		GeniResult<MailEntity> grm = mailMessageService.getMessage(msgId);
		MailEntity me = grm.getObject();
		if (null == me) {
			throw new BizException("待处理邮件已经不存在");
		}
		String content = me.getSubject();

		// 3. 登录认证
		clientLogin();

		// 3. 循环发送
		for (SmsOpLog log : colResult.getDataSet()) {
			Long phoneNo = log.getPhoneNo();
			logger.debug("{}, 正在处理 msgId:{}, phoneNo:{}", new Object[] {
					methodName, log.getMessageId(), phoneNo });
			// 2.1 更新处理中
			operLogService.update2Processing(log.getMessageId(), phoneNo);

			// 2.2 发送短信
			String ss = sendSMS(content, phoneNo);
			logger.debug("{}, 发送短信返回：{}", methodName, ss);
			// 2.3 发送完成后更新短信序列
			operLogService.updateSmsSerail(msgId, phoneNo, ss);
			
			// 发送完短信则休息半秒中
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.debug("系统异常", e);
				throw new BizException(e.getMessage());
			}
		}

		return result;
	}

	/**
	 * 发送短信处理
	 * 
	 * @param content
	 * @param phoneNo
	 */
	private String sendSMS(String content, Long phoneNo) {
		// 1.直接发送
		String ss = anthenService.getMsgClient()
				.sendMsg(anthenService.getMsgClient(), 0, phoneNo.toString(),
						content, 1);

		try {
			// 连接出现异常，需要重新发送
			while ("16".equals(ss)) {
				// 2. 断开连接
				anthenService.getMsgClient().closeConn();

				// 3. 等待一分钟后重新连接
				Thread.sleep(60000);
				logger.debug("sendSMs, 等待一分钟后重新连接。。。。");
				clientLogin();

				ss = sendSMS(content, phoneNo);
			}
		} catch (InterruptedException e) {
			// 线程异常
			logger.debug("系统异常", e);
			return "0";
		}
		return ss;
	}

	// public void handleForward(String content) {
	// logger.debug("Entering {}", "handleForward");
	// // 1. 获取当前待发送的手机号队列
	// List<PhoneItem> list = getPhoneList();
	//
	// // 2. 创建发送短信线程任务
	// NetMsgclient client = anthenService.getMsgClient();
	// logger.debug("认证成功, phoneList has next=" +
	// phoneList.iterator().hasNext());
	// for (PhoneItem phoneItem : list) {
	// logger.debug("发送短信，手机号：{}， 短信内容：{}", phoneItem.getPhoneNo(), content);
	// // 发送短信记录操作日志
	// client.sendMsg(client, 0, phoneItem.getPhoneNo().toString(), content, 1);
	// try {
	// // 每秒上限提交2条
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	//
	// }
	// }
	//
	// logger.debug("Exiting {}", "handleForward");
	// }

	public static String getAnnot() {
		return SEND_SMS;
	}
}
