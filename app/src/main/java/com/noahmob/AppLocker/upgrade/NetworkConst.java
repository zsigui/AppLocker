package com.noahmob.AppLocker.upgrade;

public class NetworkConst {
    public static final String ACTION_DOWNLOAD = "download_apk";
    public static final String ACTION_FEEDBACK = "action_feedback";
    public static String APK_TYPE = null;
    public static final String CHANNEL_META_DATE_NAME = "NOAHAPP_CHANNEL";
    public static final String CHECK_UPDATE_APK_URL = "http://api.noahmob.com/client/apk/get.php";
    public static final String CHECK_UPDATE_APK_URL_TEST = "http://api.noahmob.com/client/apk/gettest.php";
    public static final int CONNECTION_TIMEOUT = 2000;
    public static final String DEVELOP_ADDR = "http://10.127.129.88:8000";
    public static final String DOWNLOAD_APK_NAME = "Nlocker";
    public static final String EXTRA_DOWNLOAD_SHOW_TOAST = "show_toast";
    public static final String EXTRA_DOWNLOAD_URL = "download_url";
    public static final String EXTRA_DOWNLOAD_VERSIONNAME = "version_name";
    public static final String FEEDBACK_CONTENT = "feedback_content";
    public static final String FEEDBACK_EMAIL = "feedback_email";
    public static String FEEDBACK_URL_ADDRESS = null;
    public static final int NETWORK_NOT_AVAILABLE = 1;
    public static final int NETWORK_RESPONE_SUCESS = 0;
    public static final int NETWOR_FAILED = 2;
    public static final String PUBLISH_ADDR = "http://receive.noahmob.com";
    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_OK = 200;
    public static final int SOCKET_TIMEOUT = 2000;
    public static final int TYPE_DOCTOR = 2;
    public static final String UPDATE_CODE_NO_LATEST_VERSION = "101";
    public static final String UPDATE_CODE_OK = "100";
    public static final String UPDATE_CODE_PARAMS_ERROR = "102";
    public static final String UPDATE_CODE_PROGRAM_EXCEPTION = "103";
    public static String UPDATE_PUBLISH_URL;
    public static final Mode mode = Mode.publish;

    public enum HttpParam {
        date,
        serial,
        content,
        channelId,
        versionId,
        statistics,
        phoneModelName,
        resolutionName,
        androidVersion,
        email,
        type
    }

    public enum Mode {
        development,
        publish
    }

    static {
        String addr;
        FEEDBACK_URL_ADDRESS = "/client/typefeedback/add.php";
        UPDATE_PUBLISH_URL = CHECK_UPDATE_APK_URL;
        APK_TYPE = DOWNLOAD_APK_NAME;
        if (mode == Mode.publish) {
            addr = PUBLISH_ADDR;
        } else {
            addr = DEVELOP_ADDR;
            APK_TYPE = DOWNLOAD_APK_NAME;
            UPDATE_PUBLISH_URL = CHECK_UPDATE_APK_URL_TEST;
        }
        FEEDBACK_URL_ADDRESS = addr + FEEDBACK_URL_ADDRESS;
    }
}
