package simbot.example.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.OnGroupAddRequest;
import love.forte.simbot.annotation.OnGroupMemberIncrease;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupAddRequest;
import love.forte.simbot.api.message.events.GroupMemberIncrease;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.api.sender.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ����һ�� �Զ�ͨ����Ⱥ���벢�Զ�ӭ�� ��ʵ����������
 *
 * @author ForteScarlet
 */
@Beans
public class MyNewGroupMemberListen {

    /**
     * ע��õ�һ����Ϣ������������
     */
    @Depend
    private MessageContentBuilderFactory messageBuilderFactory;

    /**
     * ����������Ⱥ�����ʱ���������Ϣ��
     */
    private static final Map<String, String> REQUEST_TEXT_MAP = new ConcurrentHashMap<>();

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyNewGroupMemberListen.class);

    /**
     * {@link OnGroupAddRequest} ��һ��ģ��ע�⣬���ͬ�� {@code @Listen(GroupAddRequest.class)},
     * ������һ�� {@link GroupAddRequest} ���͵��¼���
     * <p>
     * {@link GroupAddRequest} ����˼�壬�� ��Ⱥ������롱 �¼���
     * <p>
     * ����¼�������������˼���ĳȺ��Ҳ�п��ܴ����������뵱ǰbot��Ⱥ��
     * <p>
     * ��Ȼ�ˣ�����Ǵ��������˵ļ�Ⱥ���룬��ô���bot�����Ǹ�����Ա���ܽ��յ������¼���
     *
     * @param groupAddRequest Ⱥ�������/�����¼���
     * @param setter          һ������ͨ�����룬ʹ�õ���Setter����Ȼ����Ҳ����ʹ�� {@link love.forte.simbot.api.sender.MsgSender#SETTER}, �������������ͬһ������
     * @see GroupAddRequest
     */
    @OnGroupAddRequest
    public void onRequest(GroupAddRequest groupAddRequest, Setter setter) {
        // ���¼��ġ������ߡ�
        AccountInfo accountInfo = groupAddRequest.getRequestAccountInfo();
        // �յ����¼���bot
        BotInfo botInfo = groupAddRequest.getBotInfo();

        // ����������ߵ��˺Ų���ͬ����˵�����¼�����bot�����룬���Ǳ���������Ⱥ��
        // �ⲽ�жϲ����ƺ��ܷ�����δ���汾���ܻ��ṩ�����ķ���
        // ������кõĵ��ӣ�����ͨ�� github issue �� github pr��simbot����� https://github.com/ForteScarlet/simpler-robot
        if (!accountInfo.getAccountCode().equals(botInfo.getBotCode())) {
            // ��ȡ��Ⱥ��ʱ���������Ϣ������еĻ�
            String text = groupAddRequest.getText();
            if (text != null) {
                // ����У���¼��һ����Ϣ��
                REQUEST_TEXT_MAP.put(accountInfo.getAccountCode(), text);
            }
            GroupInfo groupInfo = groupAddRequest.getGroupInfo();

            LOGGER.info("{}({}) �������Ⱥ {}({}), ���뱸ע��{}",
                    accountInfo.getAccountNickname(), accountInfo.getAccountCode(),
                    groupInfo.getGroupName(), groupInfo.getGroupCode(),
                    text
            );


            // ͨ������
            // ͨ��setter��ͨ����Ⱥ�����ж��������
            // ����1��acceptGroupAddRequest(flag)
            // flag �������¼���һ������ʶ��
            setter.acceptGroupAddRequest(groupAddRequest.getFlag());

            // ����2��setGroupAddRequest(flag, agree, blockList, why)
            // 4�������ֱ������ʶ���Ƿ�ͬ�⡢�Ƿ���������(һ����ֻ���ھܾ�ʱ��Ч, ����miraiĿǰ��֧�ִ˲���)���Լ���ô������ԭ��(һ�����ھܾ�ʱ��Ч, ����Ϊnull)
            // setter.setGroupAddRequest(groupAddRequest.getFlag(), true, false, null);

            // ����3��return Reply.accept()
            // ����������ֵ����Ϊ ReplyAble ���� Reply, Ȼ��ֱ�ӷ��� Reply.accept() ʵ��������ͬ�����롣
            // ���ַ�������������Ӧֵ���������ĵ��ο���https://www.yuque.com/simpler-robot/simpler-robot-doc/aioxhh

            // ����4������Ŀǰ Flag �������Ȼ��һЩ���պ�������δ�����ܻ��ṩ��������ݵķ�ʽ������¼����д���

        }

    }


    /**
     * ������Ⱥ����֮�󣬱��� ��Ⱥ��Ա���ӡ� �¼����������Ҫʲôӭ�²��������鶼������¼��д���
     * <p>
     * ͨ�� {@link OnGroupMemberIncrease} ����Ⱥ���������¼�����Ҳ��һ��ģ��ע�⣬���Ч�� {@code @Listen(GroupMemberIncrease.class)}
     *
     * @param groupMemberIncrease Ⱥ���������¼�ʵ��
     * @param sender              ��Ȼ�ǡ�ӭ�¡�ʾ������ȻҪ����Ϣ��
     * @see GroupMemberIncrease
     */
    @OnGroupMemberIncrease
    public void newGroupMember(GroupMemberIncrease groupMemberIncrease, Sender sender) {
        // �õ�һ����Ϣ��������
        MessageContentBuilder builder = messageBuilderFactory.getMessageContentBuilder();

        // ��Ⱥ����Ϣ
        AccountInfo accountInfo = groupMemberIncrease.getAccountInfo();

        // ���Դӻ����л�ȡ����Ⱥ��ʱ������¼����Ϣ
        // �����ϣ������null����ǵ����д���
        String text = REQUEST_TEXT_MAP.remove(accountInfo.getAccountCode());

        // �������ǵ�ӭ����Ϣ�������ģ�
        /*
            @xxx ��ӭ��Ⱥ��
            �����Ⱥ������Ϣ�ǣ�xxxxxx
         */
        MessageContent msg = builder
                // at������
                .at(accountInfo)
                // tips ͨ�� \n ����
                .text(" ��ӭ��Ⱥ��\n")
                .text("�����Ⱥ������Ϣ�ǣ�").text(text)
                .build();

        // �������˵�Ⱥ��Ϣ
        GroupInfo groupInfo = groupMemberIncrease.getGroupInfo();

        // ������Ϣ
        sender.sendGroupMsg(groupInfo, msg);
    }


}
