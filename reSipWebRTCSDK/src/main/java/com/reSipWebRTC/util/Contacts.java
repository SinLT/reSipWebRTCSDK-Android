package com.reSipWebRTC.util;

public class Contacts {
    
	//public static List<Contact> contacts;
	//public static List<Friend> friends;
	//public static List<Friend> changeFriends;
	
	// 服务器测试地址
	public static final String SERVER_IP = "";
	// 正式服务器地址
    //	public static final String SERVER_IP = "";
	
	public static boolean ISCLEARED = false;
	
	public static String COUNTRY_CODE = "+86";
	
	public static String DEVICE_PRIX = "tokudu/";
	// 选择的国家标识
	public static String COUNTRY_FLAG;
	
	public static String SEARCH_SPLID = "& &";
	
	public static String ISFIRSTLOGIN = "ISFIRSTLOGIN";
	
	public static String NEEDUPLOAD = "NEEDUPLOAD";
	
	/** 消息记录的类型 短信消息／图片消息／语音消息／语音通话／视频通话 */
	public static enum MSG_TYPE {
		MSG_TEXT, MSG_PIC, MSG_AUDIO, CALL_AUDIO, CALL_VIDEO
	}
	
	// 保存的文件名字
	public static final String BEECHAT_SETTIONG = "beechat_setting";
	//保存注册帐号的名字
	public static final String BEECHAT_USER = "beechat_user";
	public static final String USER_PHONE = "USER_PHONE";
	public static final String PASSWORD = "PASSWORD";
	public static final String COUNTRY_ID = "COUNTRY_ID";
	public static final String BEECHAT_LAST_CALL_NUMBER = "public static final String BEECHAT_USER";
	// 选择联系人
	public static final String SELECT_TYPE = "SELECT_TYPE";
	/** 选择发消息好友 仅显示蜜语好友 */
	public static final String SELECT_MESSAGE = "SELECT_MESSAGE";
	/** 选择拨号好友 蜜语好友｜所有联系人*/
	public static final String SELECT_DIAL = "SELECT_DIAL";
	/** 选择邀请好友 仅显示非蜜语好友 */
	public static final String SELECT_INVITATION = "SELECT_INVITATION";
	public static final String SELECT_ITEM = "SELECT_ITEM";
	// 调用发送短信得位置
	public static final String CALLDIRECTION = "CALLEDSTART";
	public static final int CALLFROM_CONTACTDETAIL = 303;
	public static final int CALLFROM_CHATMSG = 304;
	public static final String PREF_DEVICE_ID = "PREF_DEVICE_ID";
	
	public static final String NOTIFACTION_BACKGROUND_CALL = "NOTIFACTION_BACKGROUND_CALL";
	public static final String NOTIFACTION_BACKGROUND_VIDEO_CALL = "NOTIFACTION_BACKGROUND_VIDEO_CALL";
	
    // 推送消息
	public static final String NOTIFACTION_TOCALL = "NOTIFACTION_TOCALL";
	public static final String NOTIFACTION_LOSE_CALL = "NOTIFACTION_LOSE_VIDEO";
	
	public static final String NOTIFACTION_TOVIDEO = "NOTIFACTION_TOVIDEO";
	public static final String NOTIFACTION_LOSE_VIDEO = "NOTIFACTION_LOSE_VIDEO";
	// 好友加入通知
	public static final String NOTIFACTION_FRIEND_ADD = "NOTIFACTION_FRIEND_ADD";
	public static final String NOTIFACTION_OFFLINE_MSG = "NOTIFACTION_OFFLINE_MSG";
	
	public static final String NOTIFACTION_VERSION_UPDATE = "NOTIFACTION_VERSION_UPDATE";
	public static final String NOTIFACTION_OTHRE_MSG = "NOTIFACTION_OTHRE_MSG";
	
	public static final String NOTIFACTION_NUMBER = "NOTIFACTION_NUMBER";
	public static final String NOTIFACTION_TYPE = "NOTIFACTION_TYPE";
	
	public static final String IS_UPGRADING ="IS_UPGRADING";
	/**
	 * 客服电话
	 */
	public static final String CALL = "0345788819";
	
	public static final int SELECT_RESULT = 100;
	
	// 状态码
	public static final int CLAUSERESULTCODE = 12;
	/**
	 * 服务条款内容  en
	 */
	public static final String CLAUSE_HTTP_URL_EN = "http://www.meetplus.cc/terms-of-service/en/terms.txt";
	/**
	 * 服务条款内容  cn
	 */
	public static final String CLAUSE_HTTP_URL_CN = "http://www.meetplus.cc/terms-of-service/cn/terms.txt";
	/**
	 * 服务条款内容  jp
	 */
	public static final String CLAUSE_HTTP_URL_JP = "http://www.meetplus.cc/terms-of-service/jp/terms.txt";
	
	// 手机的宽和高；
	public static final float PHONEWITH = 480;
	public static final float PHONEHEIGHT = 857;
	
	// 是否同意过服务条款
	public static volatile boolean isAgreed = false;
	
    //收件箱
	public static final String SMS_INBOX = "content://sms/inbox";
	
	// 向服务器发送的手机号码
	public static final String SYSYEMPHONE = "264505085";
    //已发送
    //  public static final String SMS_SENT  = "content://sms/sent";
	
	// 系统通讯录url
	//public static final String SYSYEMPHONE = "+85264505085";
	
	  /**联系人显示名称**/  
	public static final int PHONES_DISPLAY_NAME_INDEX = 0;
	
    /**电话号码**/  
	public static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
	public static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID索引**/  
	public static final int  PHONES_CONTACT_ID_INDEX = 1;  
	public static final int  PHONES_CONTACT_NUMBER_INDEX = 2;  
    /**联系人的时间**/  
	public static final int PHONES_CONTACT_TIME_INDEX = 6;  
	
	/** 新建联系人界面 */
	public static final String TYPE_DIR_PERSION = "vnd.android.cursor.dir/person";  
	public static final String TYPE_DIR_CONTACT = "vnd.android.cursor.dir/contact";  
	public static final String TYPE_DIR_RAW_CONTACT = "vnd.android.cursor.dir/raw_contact"; 
	
	/** 传值得key */
	public static final String KEY_CONTACT_ID = "KEY_CONTACT_ID";
	public static final String KEY_CONTACT = "KEY_CONTACT";
	public static final String KEY_CONTACT_BUNDLE = "KEY_CONTACT_BUNDLE";
	//话单详情页
	public static final String KEY_LOG = "KEY_LOG";
	/** beechat 电话类型*/
	public static final String PHONETPYE_BEECHAT = "PHONETPYE_BEECHAT";

	public static final String PHONECALLID = "PHONECALLID";

	/** 电话状态 **/
	public static final String PHONESTATE = "PHONESTATE";
	public static final String PHONEMSG = "PHONEMSG";
	public static final String PHONENAME = "PHONENAME";
	public static final String PHONNUMBER = "PHONNUMBER";
	public static final String PHONEFRONT = "PHONEFRONT";
	public static final String ACTION_FROM_SERVICE = "ACTION_FROM_SERVICE";
	public static final int NOTIFICATION_ID = 11;
	public static final int NOTIFICATION_CALL_ID = 12;
	public static final int NOTIFICATION_FRIEND_JOIN = 13;
	public static final int PHONESTATE_INCOMMING = 11;
	public static final int INVITE_VOICE_REQUEST = 22;
	public static final int PHONESTATE_TALKING = 24;
	public static final int CALL_PROCESSING = 25;
	public static final int CALL_RINING = 26;
	public static final int CALL_CONNECT = 27;
	public static final int CALL_HOLDDING = 28;
	// 恢复通话
	public static final int CALL_RESUME = 29;
	// 结束通话
	public static final int CALL_DISCONNECT = 30;
	// 网络状况
	public static final int NETWORK_DELAY = 31;
	// 呼叫失败
	public static final int CALL_FAILED = 32;
	
	public static final int CALL_VIDEO_STREAM_READY = 33;
	// 收到视频请求
	public static final int RECEIVE_VIDEO_REQUEST = 35;
	// 邀请某人视频
    public static final int INVITE_VIDEO_REQUEST = 36;

 	public static final int ACTION_FROM_PHONE_SERVICE = 40;
	public static final int CALL_HOLDED = 40;
	public static final int CALL_RESUMED = 41;
	public static final int CALL_MEDIA_CONNECTED = 42;
	
	public static final String BROADCAST_CATION = "com.cloudrtc.CallState";
	public static final String CREATE_USER_STATE = "CREATE_USER_STATE";
	public static final int CHECKAUTHCODE = 50;
	public static final int FRIENDLIST = 51;
	public static final int ONCREATEUSER = 52;
	public static final int MODIFYUSERINFO = 53;
	public static final int DEACTIVE_USER = 54;
	
	public static final String SMS_SEND_Fail_KEY = "SMS_SEND_SUCCESS_KEY";
	
	public static final int SMS_SEND_SUCCESS = 100;
	/**
	 *  reg_status CreateSuccess 创建成功, DBConnectFailed 服务器内部错误,ExceedMaxRegisterTimes 超过当日最多注册次数, BadParams 参数错误, UnknownError 其它未知错误
	 */
	/**
	 * 发送短信返回状态值 创建成功
	 */
	public static final String SMS_RETURN_STATUS_SUCCESS = "CreateSuccess";
	
	public static final String SMS_RETURN_STATUS_BADPAYLOAD = "BadPayload";
	
	public static final String MSG_SENDING = "0";
	public static final String MSG_SENDED = "1";
	public static final String MSG_SENDED_FAILED = "2";
	public static final String MSG_ARRIVED = "3";
	
	/**
	 * 发送短信返回状态值 服务器内部错误
	 */
	public static final String SMS_RETURN_STATUS_DBFAIL = "DBConnectFailed";
	/**
	 * 发送短信返回状态值 超过当日最多注册次数
	 */
	public static final String SMS_RETURN_STATUS_TRY_MORE_TIMES = "ExceedMaxRegisterTimes";
	/**
	 * 发送短信返回状态值 参数错误
	 */
	public static final String SMS_RETURN_STATUS_BAD_PARAMS = "BadParams";
	
	/**
	 * 发送短信返回状态值 其它未知错误
	 */
	public static final String SMS_RETURN_STATUS_UK_ERROR = "UnknownError";
	
	/**  
	 * 隐私策略 cn  
	 */
	public static final String PRIVATE_POLICY_CN = "http://www.meetplus.cc/privacy-policy/cn/";
	/**  隐私策略 en  **/
	public static final String PRIVATE_POLICY_EN = "http://www.meetplus.cc/privacy-policy/en/";
	/**  隐私策略 cn  **/
	public static final String PRIVATE_POLICY_JP = "http://www.meetplus.cc/privacy-policy/jp/";
	
	/**
	 * 设置界面内容
	 */
	
	/**
	 * 当没有签名时候的字段
	 */
	
	
	
	/***************************  设置界面 状态变量  *****************************/
	
	/**
	 * 是否加入好友
	 */
	public static boolean set_beechat_friend_join = true;
	
	/**
	 * 是否加入好友 key
	 */
	public static final String SET_BEECHAT_FRIEND_JOIN_KEY = "SET_BEECHAT_FRIEND_JOIN_KEY";
	
	
	/**
	 * 是否文本消息浏览
	 */
	public static boolean set_beechat_text_message_view = true;
	
	/**
	 * 是否文本消息浏览 key
	 */
	public static final String SET_BEECHAT_TEXT_MESSAGE_VIEW_KEY = "SET_BEECHAT_TEXT_MESSAGE_VIEW_KEY";
	
	/**
	 * 是否允许振铃
	 */
	public static boolean set_beechat_ringing = true;
	
	/**
	 * 是否允许振铃 key
	 */
	public static final String SET_BEECHAT_RINGING_KEY = "SET_BEECHAT_RINGING_KEY";
	
	/**
	 * 是否允许震动
	 */
	public static boolean set_beechat_shock = true;
	
	/**
	 * 是否允许震动 key
	 */
	public static final String SET_BEECHAT_SHOCK_KEY = "SET_BEECHAT_SHOCK_KEY";
	
	/**
	 * 是否允许访问电话薄
	 */
	public static boolean set_beechat_call_phonebook = true;
	
	/**
	 * 是否允许访问电话薄 key
	 */
	public static final String SET_BEECHAT_CALL_PHONEBOOK_KEY = "SET_BEECHAT_CALL_PHONEBOOK_KEY";
	
	/***************************  更多界面  头像存储相关  *****************************/
	/**
	 * 存储文件夹名称
	 */
	public static final String HEAD_IMAGE_STORE_PATH = "/MeetPlus/headImage/";
	
	/**
	 *  图片类型
	 */
	public static final int BITMAP_TYPE_ROUND = 1;
	public static final int BITMAP_TYPE_NOROUND = 0;
	public static final int BITMAP_TPYE_HEAD = 2;
	public static final int BITMAP_TYPE_CHAT = 3;
	
	
	/**
	 * 头像图片的大小
	 */
	public static final int HEAD_IMAGE_SIZE = 100;
	
	
	/**
	 * 头像图片名称
	 */
	public static final String HEAD_IMAGE_NAME = "imageHead.png";
	
	/**
	 * 头像图片保存路径  editor 使用
	 */
	public static final String HEAD_IMAGE_STORE_SHAREPREFERENCES_EDITOR= "HEAD_IMAGE_STORE_SHAREPREFERENCES_EDITOR";
	
	/**
	 * 头像图片路径 在SharepreFerences
	 */
	public static final String HEAD_IMAGE_STORE_SHAREPREFERENCES = "HEAD_IMAGE_STORE_SHAREPREFERENCES";
	
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_SINGTURE = "USER_SINGTURE";
	public static final String USER_HEAD = "USER_HEAD";
	
	/**
	 *   拍照和获取图片的返回值
	 */
	public static  final int ACTIVITY_PHOTO = 1001;
	
	public static  final int ACTIVITY_PICTURE = 1002;
	
	
	/**
	 * 存放更多界面用户信息标志
	 */
	public static final String MORE_USERMSG = "MORE_USERMSG";
	
	/**
	 * 存放更多界面用户信息 Bundle
	 */
	public static final String MORE_USERMSG_BUNDLE = "MORE_USERMSG_BUNDLE";
	
	/********************            访问电话薄            ********************/
	
	
	/**
	 * 选择地区号  intent key
	 */
	public static final String AREA_ID_INTENT_KEY = "AREA_ID_INTENT_KEY";
	
	/**
	 * 电话区号  shareperference
	 */
	public static final String SHARE_AREA_CODE ="SHARE_AREA_CODE";
	
}
