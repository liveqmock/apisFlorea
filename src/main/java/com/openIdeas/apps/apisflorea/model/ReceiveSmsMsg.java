package com.openIdeas.apps.apisflorea.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linkage.netmsg.server.AnswerBean;
import com.linkage.netmsg.server.ReceiveMsg;
import com.linkage.netmsg.server.ReturnMsgBean;
import com.linkage.netmsg.server.UpMsgBean;
import com.openIdeas.apps.apisflorea.intf.MailMessageServiceIntf;
import com.openIdeas.apps.apisflorea.mail.RemoteMailServiceIntf;
import com.openIdeas.apps.apisflorea.result.Result;

@Service
public class ReceiveSmsMsg extends ReceiveMsg {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RemoteMailServiceIntf remoteMail;

	@Autowired
	private MailMessageServiceIntf mailMessageService;

	/* 获取下行短信返回状态和短信ID的方法 */
	public void getAnswer(AnswerBean answerBean) {
		super.getAnswer(answerBean);
		/* 序列Id */
		// String seqIdString = answerBean.getSeqId();
		// /* 短信状态 ,0表示提交至API平台成功 */
		int status = answerBean.getStatus();
		// /* 下行短信ID，用来唯一标识一条下行短信 */
		String msgId = answerBean.getMsgId();

		if (0 == status) {
			logger.info("{} 邮件提交短信平台成功", msgId);
			// 设置邮件已读
			Result r = remoteMail.reserveMail(msgId);
			
			if (r.isSuccess()) {
				// 删除数据库邮件记录
				mailMessageService.deleteById(msgId);
			}
		}
	}

	/* 接收上行短信的方法 */
	public void getUpMsg(UpMsgBean upMsgBean) {
		super.getUpMsg(upMsgBean);
	}

	/* 获取下行短信回执的方法 */
	public void getReturnMsg(ReturnMsgBean returnMsgBean) {
		super.getReturnMsg(returnMsgBean);

		// String sequenceId = returnMsgBean.getSequenceId();
		// /* 短信的msgId */
		// String msgId = returnMsgBean.getMsgId();
		// /* 发送号码 */
		// String sendNum = returnMsgBean.getSendNum();
		// /* 接收号码 */
		// String receiveNum = returnMsgBean.getReceiveNum();
		// /* 短信提交时间 */
		// String submitTime = returnMsgBean.getSubmitTime();
		// /* 短信下发时间 */
		// String sendTime = returnMsgBean.getSendTime();
		// /* 短信状态 */
		// String msgStatus = returnMsgBean.getMsgStatus();
		// /* 短信错误代码 */
		// int msgErrStatus = returnMsgBean.getMsgErrStatus();

		// 此处加入接收短信回执的处理代码
		// System.out.println("ReturnMsgBean sequenceId: " + sequenceId);
		// System.out.println("ReturnMsgBean msgId: " + msgId);
		// System.out.println("ReturnMsgBean sendNum: " + sendNum);
		// System.out.println("ReturnMsgBean receiveNum: " + receiveNum);
		// System.out.println("ReturnMsgBean submitTime: " + submitTime);
		// System.out.println("ReturnMsgBean sendTime: " + sendTime);
		// System.out.println("ReturnMsgBean msgStatus: " + msgStatus);
		// System.out.println("ReturnMsgBean msgErrStatus: " + msgErrStatus);
	}

}
