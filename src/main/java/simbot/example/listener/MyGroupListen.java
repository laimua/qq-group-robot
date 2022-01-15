package simbot.example.listener;

import catcode.CatCodeUtil;
import catcode.Neko;
import catcode.codes.LazyMapNeko;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.*;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupAccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.GroupMsgRecall;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.api.sender.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simbot.example.persistence.domain.KeyWordBan;
import simbot.example.persistence.domain.PointLog;
import simbot.example.persistence.domain.User;
import simbot.example.persistence.service.IKeyWordBanService;
import simbot.example.persistence.service.IPointLogService;
import simbot.example.persistence.service.IUserService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 群消息监听的示例类。
 * 所有需要被管理的类都需要标注 {@link Service} 注解。
 *
 * 由于当前是处于springboot环境下，因此强烈建议类上的注释使用：
 * <ul>
 *     <li>{@link org.springframework.stereotype.Component}</li>
 *     <li>{@link Service}</li>
 * </ul>
 * 等注解来代替simbot的 {@link Beans}。
 *
 * 同样的，依赖注入也请使用 {@link org.springframework.beans.factory.annotation.Autowired} 等Springboot相关的注解。
 *
 * @author ForteScarlet
 */
@Service
public class MyGroupListen {

    /** log */
    private static final Logger LOG = LoggerFactory.getLogger(MyGroupListen.class);
    public static List<KeyWordBan> oplistsecl = null;
   /* static {
        System.out.println("构造了监听器===============================");
         oplistsecl = readFileContent("./src/main/java/simbot/example/原神常用词库.txt");
    }*/

    @Autowired
    private MessageContentBuilderFactory builderFactory;
    @Autowired
    IKeyWordBanService keyWordBanService;
    @Autowired
    IUserService userService;
    @Autowired
    IPointLogService pointLogService;

    @Listen(GroupMsgRecall.class)
    public void listenCallBack(GroupMsgRecall msg, MsgSender sender){
        // 获取发消息的人。
        AccountInfo accountInfo = msg.getAccountInfo();
        sender.SENDER.sendGroupMsg(msg.getGroupInfo().getGroupCode(),accountInfo.getAccountNickname()+"撤回了消息：");
        sender.SENDER.sendGroupMsg(msg.getGroupInfo().getGroupCode(), msg.getMsgContent());
//        sender.sendGroupMsg(msg.getGroupInfo().getGroupCode(),accountInfo.getAccountNickname()+"撤回了消息："+msgContent);
    }

    private static final String APP_KEY="2f0de51cc2317ad9dc92fc811ac99e4b"; //天行机器人API

    private static Map<String,Integer> signMap = new HashMap<>(); //已注册用户

    private static final int FIRST_SECTION = 0;
    private static final int SECOND_SECTION = 10;
    private static final int THIRD_SECTION = 20;
    private static final int FOURTH_SECTION = 50;

    private static final int FIRST_POINT = 20;
    private static final int SECOND_POINT = 10;
    private static final int THIRD_POINT = 5;

    //判断用户进入活跃度加分
    public void chickActivityPoint(String code){
        if(signMap.containsKey(code)){
            Integer todayNum = signMap.get(code);
            User user = userService.selectUserByCode(code);
            PointLog log = new PointLog();
            log.setPointType("activity");
            log.setChangeType("add");
            if(todayNum>=FIRST_SECTION&&todayNum<=SECOND_SECTION){
                signMap.put(code,todayNum+1);
                user.setCurrentPoint(user.getCurrentPoint()+FIRST_POINT);
                user.setTotalPoint(user.getTotalPoint()+FIRST_POINT);
                userService.updateUser(user);
                log.setCode(code);
                log.setChangePoint(FIRST_POINT+"");
                pointLogService.insertPointLog(log);
            }else if(todayNum>SECOND_SECTION&&todayNum<=THIRD_SECTION){
                signMap.put(code,todayNum+1);
                user.setCurrentPoint(user.getCurrentPoint()+SECOND_POINT);
                user.setTotalPoint(user.getTotalPoint()+SECOND_POINT);
                userService.updateUser(user);
                log.setCode(code);
                log.setChangePoint(SECOND_POINT+"");
                pointLogService.insertPointLog(log);
            }else if(todayNum>THIRD_SECTION&&todayNum<=FOURTH_SECTION){
                signMap.put(code,todayNum+1);
                user.setCurrentPoint(user.getCurrentPoint()+THIRD_POINT);
                user.setTotalPoint(user.getTotalPoint()+THIRD_POINT);
                userService.updateUser(user);
                log.setCode(code);
                log.setChangePoint(THIRD_POINT+"");
                pointLogService.insertPointLog(log);
            }
        }
    }

    //装载已注册用户
    public void chickSignPerson(){
        if(signMap!=null&&signMap.size()>0){

        }else{
            User user = new User();
            List<User> users = userService.selectUserList(user);
            for (User u:
            users) {
                signMap.put(u.getCode(),u.getTodayNum());
            }
        }
        System.out.println(signMap);
    }


    //启动自动回复机器人
    public void startAnswerBot(String msg,GroupMsg groupMsg,Sender sender){
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
        builder.clear();
        String httpUrl = "http://api.tianapi.com/robot/index";
        String httpArg = "key="+APP_KEY+"&question="+msg;
        String jsonResult = request(httpUrl,httpArg);
        JSONObject obj = JSONObject.parseObject(jsonResult);
        String code = obj.getString("code");
        if(code.equals("200")){
            JSONArray newslist = obj.getJSONArray("newslist");
            if(newslist.size()>0){
                JSONObject answer = newslist.getJSONObject(0);
                String reply = answer.getString("reply");
                MessageContent msgsend = builder.at(groupMsg.getAccountInfo().getAccountCode()).text(reply).build();
                sender.sendGroupMsg(groupMsg,msgsend);
            }
        }else {
            MessageContent msgsend = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("我今天很累了，明天再问我吧QVQ").build();
            sender.sendGroupMsg(groupMsg,msgsend);
        }
    }

    @OnGroup
    public void listen(GroupMsg groupMsg,MsgSender msgSender){
        boolean flag = false; //false为正常执行，true为阻断
        chickKeyWordList(); //检查是否加载屏蔽词库
        chickSignPerson(); // 装载已注册用户
        chickActivityPoint(groupMsg.getAccountInfo().getAccountCode());//活跃度加分
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder(); //消息构造器
        Setter setter = msgSender.SETTER;
        Getter getter = msgSender.GETTER;
        Sender sender = msgSender.SENDER;
            boolean callBot = atBot(groupMsg); //判断是否@了机器人 true为是，false为否
            if(callBot){
                String command = todo(groupMsg.getText());
                if(command.equals(ADD_KEY_WORD)){//添加关键词
                    addKeyWord(groupMsg,sender);
                }else if(command.equals(SIGN)){//注册
                    boolean banflag =ban(groupMsg,setter,sender,builder);
                    if (!banflag){
                        sign(groupMsg,sender);
                    }
                }else if(command.equals(QUERY_SIGN)){ //查询注册信息
                    boolean banflag =ban(groupMsg,setter,sender,builder);
                    if (!banflag){
                        querySign(groupMsg,sender);
                    }
                }else if(command.equals(UPDATE_NICK)){ //修改昵称
                    boolean banflag =ban(groupMsg,setter,sender,builder);
                    if (!banflag){
                        updateNick(groupMsg,sender);
                    }
                }else if(command.equals(IS_NOT_COMMAND)){ //不是指令消息，则启动自动聊天回答
                    boolean banflag =ban(groupMsg,setter,sender,builder);
                    if (!banflag){
                        startAnswerBot(groupMsg.getText(),groupMsg,sender);
                    }
                }else {
                    ban(groupMsg,setter,sender,builder); // 查询关键词，执行禁言 true为执行了禁言，false为放行
                }
            }else {
                ban(groupMsg,setter,sender,builder); // 查询关键词，执行禁言 true为执行了禁言，false为放行
            }
    }

    //修改昵称
    public void updateNick(GroupMsg groupMsg, Sender sender){
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
        builder.clear();
        String newNick = groupMsg.getText().split("：")[1];
        try {
            User user = userService.selectUserByCode(groupMsg.getAccountInfo().getAccountCode());
            if(user!=null){
                String oldNick = user.getNick();
                user.setNick(newNick);
                userService.updateUser(user);
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("昵称已从【"+oldNick+"】修改为【"+newNick+"】！").build();
                sender.sendGroupMsg(groupMsg,msg);
            }else {
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("您未注册账号！").build();
                sender.sendGroupMsg(groupMsg,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("修改失败！请联系管理员！").build();
            sender.sendGroupMsg(groupMsg,msg);
        }
    }

    //注册查询
    public void querySign(GroupMsg groupMsg, Sender sender){
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
        builder.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String code = groupMsg.getText().split("：")[1];
        try {
            User user = userService.selectUserByCode(code);
            if(user!=null){
                MessageContent msg = builder
                        .at(groupMsg.getAccountInfo().getAccountCode())
                        // tips 通过 \n 换行
                        .text("\n")
                        .text("昵称："+user.getNick()+"\n")
                        .text("QQ："+user.getCode()+"\n")
                        .text("创建日期："+sdf.format(user.getCreateDate())+"\n")
                        .text("权限："+user.getJurisdiction()+"\n")
                        .text("宠物："+"暂无"+"\n")
                        .text("等级："+user.getLevel()+"\n")
                        .text("特性："+user.getCharacteristic()+"\n")
                        .text("总积分："+user.getTotalPoint()+"\n")
                        .text("当前积分："+user.getCurrentPoint()+"\n")
                        .build();
                sender.sendGroupMsg(groupMsg,msg);
            }else {
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("查询失败！用户不存在！").build();
                sender.sendGroupMsg(groupMsg,msg);
            }
        }catch (Exception e){
            MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("查询失败！请联系管理员！").build();
            sender.sendGroupMsg(groupMsg,msg);
        }


    }

    //注册
    public void sign(GroupMsg groupMsg,Sender sender){
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
        builder.clear();
        User old = userService.selectUserByCode(groupMsg.getAccountInfo().getAccountCode());
        if (old!=null){
            MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("您的账号已存在！").build();
            sender.sendGroupMsg(groupMsg,msg);
        }else{
            try {
                String nick = groupMsg.getText().split("：")[1];
                User user = new User();
                user.setCode(groupMsg.getAccountInfo().getAccountCode());
                user.setNick(nick);
                userService.insert(user);
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("注册成功！昵称：【"+nick+"】").build();
                sender.sendGroupMsg(groupMsg,msg);
                signMap.put(groupMsg.getAccountInfo().getAccountCode(),0); //注册成功后填入装载列表
            }catch (Exception e){
                e.printStackTrace();
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("注册失败！请联系管理员！").build();
                sender.sendGroupMsg(groupMsg,msg);
            }
        }
    }

    //添加关键词
    public void addKeyWord(GroupMsg groupMsg,Sender sender){
        boolean todo = false;
        if(groupMsg.getAccountInfo().getAccountCode().equals("435107605")){
            todo = true;
        }
        MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
        builder.clear();
        if(todo){
            String keyowrd = groupMsg.getText().split("：")[1];
            KeyWordBan ban = new KeyWordBan();
            ban.setKeyWord(keyowrd);
            List<KeyWordBan> list = keyWordBanService.selectKeyWordList(ban);
            if(list.size()>0){
                MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("关键词：【"+keyowrd+"】已存在！").build();
                sender.sendGroupMsg(groupMsg,msg);
            }else{
                try {
                    KeyWordBan inBan = new KeyWordBan();
                    inBan.setKeyWord(keyowrd);
                    keyWordBanService.insert(inBan);
                    MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("关键词：【"+keyowrd+"】添加成功！").build();
                    sender.sendGroupMsg(groupMsg,msg);
                    //添加成功后，重新获取词库
                    KeyWordBan bannew = new KeyWordBan();
                    oplistsecl = keyWordBanService.selectKeyWordList(bannew);
                }catch (Exception e){
                    e.printStackTrace();
                    MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("系统异常，添加失败！").build();
                    sender.sendGroupMsg(groupMsg,msg);
                }
            }
        }else {
            MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("能不能操作我你心里没数吗？").build();
            sender.sendGroupMsg(groupMsg,msg);
        }

    }

    public static final String ADD_KEY_WORD = "添加关键词";
    public static final String SIGN = "注册";
    public static final String QUERY_SIGN = "查询";
    public static final String UPDATE_NICK = "修改昵称";
    public static final String IS_NOT_COMMAND = "未识别到指令";
    public static final String OTHER_WORD = "其他指令";
    //判断执行指令
    public String todo(String command){
        String[] comstr = command.split("：");
        if(comstr.length>1){
            if (comstr[0].trim().equals(ADD_KEY_WORD)){
                return ADD_KEY_WORD;
            }else if(comstr[0].trim().equals(SIGN)){
                return SIGN;
            }else if(comstr[0].trim().equals(UPDATE_NICK)){
                return UPDATE_NICK;
            }else if(comstr[0].trim().equals(QUERY_SIGN)){
                return QUERY_SIGN;
            }else {
                return OTHER_WORD;
            }
        }else{
            return IS_NOT_COMMAND;
        }
    }

    //判断是否@机器人
    public boolean atBot(GroupMsg groupMsg){
        boolean flag = false;
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        Neko atNeko = util.getNeko(groupMsg.getMsg());
        MessageContent catmsg = groupMsg.getMsgContent();
        for (Neko cat:
                catmsg.getCats()) {
            if (cat.getType().equals("at")&& atNeko.get("code").equals(groupMsg.getBotInfo().getBotCode())){
                flag=true;
            }
        }
        return flag;
    }

    //关键词禁言
    public boolean ban(GroupMsg groupMsg, Setter setter,Sender sender,MessageContentBuilder builder){
        boolean ban = false;
        for (KeyWordBan op:
                oplistsecl) {
            if(groupMsg.getText().indexOf(op.getKeyWord().trim())!=-1){
                System.out.println("检测到关键词："+op);
                ban =true;
            }
        }
        if (ban){
            setter.setGroupBan(groupMsg.getGroupInfo().getGroupCode(),groupMsg.getAccountInfo().getAccountCode(),1, TimeUnit.MINUTES);
            MessageContent msg = builder.at(groupMsg.getAccountInfo().getAccountCode()).text("不可以当OP哦！").build();
            sender.sendGroupMsg(groupMsg.getGroupInfo().getGroupCode(),msg);
        }
        return ban;
    }

    //装载屏蔽词库
    public void chickKeyWordList(){
        if(oplistsecl!=null){

        }else{
            KeyWordBan ban = new KeyWordBan();
            oplistsecl = keyWordBanService.selectKeyWordList(ban);
        }

    }


    public String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List list = new ArrayList();
        try {
            reader = new BufferedReader(new FileReader(file));
            String readStr;
            while ((readStr = reader.readLine()) != null) {
                list.add(readStr.split(" ")[readStr.split(" ").length-1]);
            }
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return list;
    }
    /**
     * 此监听函数代表，收到消息的时候，将消息的各种信息打印出来。
     *
     * 此处使用的是模板注解 {@link OnGroup}, 其代表监听一个群消息。
     *
     * 由于你监听的是一个群消息，因此你可以通过 {@link GroupMsg} 作为参数来接收群消息内容。
     */
  /*  @OnGroup
    public void onGroupMsg(GroupMsg groupMsg) {
        // 打印此次消息中的 纯文本消息内容。
        // 纯文本消息中，不会包含任何特殊消息（例如图片、表情等）。
        System.out.println(groupMsg.getText());

        // 打印此次消息中的 消息内容。
        // 消息内容会包含所有的消息内容，也包括特殊消息。特殊消息使用CAT码进行表示。
        // 需要注意的是，绝大多数情况下，getMsg() 的效率低于甚至远低于 getText()
        System.out.println(groupMsg.getMsg());

        // 获取此次消息中的 消息主体。
        // messageContent代表消息主体，其中通过可以获得 msg, 以及特殊消息列表。
        // 特殊消息列表为 List<Neko>, 其中，Neko是CAT码的封装类型。

        MessageContent msgContent = groupMsg.getMsgContent();

        // 打印消息主体
        System.out.println(msgContent);
        // 打印消息主体中的所有图片的链接（如果有的话）
        List<Neko> imageCats = msgContent.getCats("image");
        System.out.println("img counts: " + imageCats.size());
        for (Neko image : imageCats) {
            System.out.println("Img url: " + image.get("url"));
        }


        // 获取发消息的人。
        GroupAccountInfo accountInfo = groupMsg.getAccountInfo();
        // 打印发消息者的账号与昵称。
        System.out.println(accountInfo.getAccountCode());
        System.out.println(accountInfo.getAccountNickname());


        // 获取群信息
        GroupInfo groupInfo = groupMsg.getGroupInfo();
        // 打印群号与名称
        System.out.println(groupInfo.getGroupCode());
        System.out.println(groupInfo.getGroupName());
    }*/


  /*  @OnGroup
    public ReplyAble listen(GroupMsg groupMsg){
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        Neko atNeko = util.getNeko(groupMsg.getMsg());
        boolean flag = false;
        MessageContent catmsg = groupMsg.getMsgContent();
        for (Neko cat:
        catmsg.getCats()) {
            if (cat.getType().equals("at")&& atNeko.get("code").equals("242211364")){
                flag=true;
            }
        }
        if (flag){
            return  Reply.reply("干嘛！",true);
        }else {
            return  null;
        }
    }*/


}
