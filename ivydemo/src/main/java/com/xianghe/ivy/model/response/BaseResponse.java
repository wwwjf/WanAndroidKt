package com.xianghe.ivy.model.response;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;

import java.io.Serializable;

/**
 * 请求接口返回数据基类
 *
 * @param <T>
 */
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -4532807624479403891L;

    public interface Status {
        int FAILED = 0;
        int OK = 1;
    }

    /**
     * status : 0
     * info : 短信验证码不正确
     * data : []
     * api_version : 1.2.0
     */
    /**
     * 0失败，1成功
     */
    private int status;
    @SerializedName("info_code")
    private int mInfoCode;
    private String info;
    private String api_version;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getInfoCode() {
        return mInfoCode;
    }

    public void setInfoCode(int infoCode) {
        mInfoCode = infoCode;
    }

    public String getInfo() {
        return infoCode2String(IvyApp.getInstance(),mInfoCode);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", api_version='" + api_version + '\'' +
                ", data=" + data +
                '}';
    }

    public static String infoCode2String(Context ctx, int infoCode) {
        switch (infoCode) {
            case 10000: return ctx.getString(R.string.info_code_10000);
            case 10001: return ctx.getString(R.string.info_code_10001);
            case 10002: return ctx.getString(R.string.info_code_10002);
            case 10003: return ctx.getString(R.string.info_code_10003);
            case 10004: return ctx.getString(R.string.info_code_10004);
            case 10005: return ctx.getString(R.string.info_code_10005);
            case 10006: return ctx.getString(R.string.info_code_10006);
            case 10007: return ctx.getString(R.string.info_code_10007);
            case 10008: return ctx.getString(R.string.info_code_10008);
            case 10009: return ctx.getString(R.string.info_code_10009);
            case 10010: return ctx.getString(R.string.info_code_10010);
            case 10011: return ctx.getString(R.string.info_code_10011);
            case 10012: return ctx.getString(R.string.info_code_10012);
            case 10013: return ctx.getString(R.string.info_code_10013);
            case 10014: return ctx.getString(R.string.info_code_10014);
            case 10015: return ctx.getString(R.string.info_code_10015);
            case 10016: return ctx.getString(R.string.info_code_10016);
            case 10017: return ctx.getString(R.string.info_code_10017);
            case 10018: return ctx.getString(R.string.info_code_10018);
            case 10019: return ctx.getString(R.string.info_code_10019);
            case 10020: return ctx.getString(R.string.info_code_10020);
            case 10021: return ctx.getString(R.string.info_code_10021);
            case 10022: return ctx.getString(R.string.info_code_10022);
            case 10023: return ctx.getString(R.string.info_code_10023);
            case 10024: return ctx.getString(R.string.info_code_10024);
            case 10025: return ctx.getString(R.string.info_code_10025);
            case 10026: return ctx.getString(R.string.info_code_10026);
            case 10027: return ctx.getString(R.string.info_code_10027);
            case 10028: return ctx.getString(R.string.info_code_10028);
            case 10029: return ctx.getString(R.string.info_code_10029);
            case 10030: return ctx.getString(R.string.info_code_10030);
            case 10031: return ctx.getString(R.string.info_code_10031);
            case 10032: return ctx.getString(R.string.info_code_10032);
            case 10033: return ctx.getString(R.string.info_code_10033);
            case 10034: return ctx.getString(R.string.info_code_10034);
            case 10035: return ctx.getString(R.string.info_code_10035);
            case 10036: return ctx.getString(R.string.info_code_10036);
            case 10037: return ctx.getString(R.string.info_code_10037);
            case 10038: return ctx.getString(R.string.info_code_10038);
            case 10039: return ctx.getString(R.string.info_code_10039);
            case 10040: return ctx.getString(R.string.info_code_10040);
            case 10041: return ctx.getString(R.string.info_code_10041);
            case 10042: return ctx.getString(R.string.info_code_10042);
            case 10043: return ctx.getString(R.string.info_code_10043);
            case 10044: return ctx.getString(R.string.info_code_10044);
            case 10045: return ctx.getString(R.string.info_code_10045);
            case 10046: return ctx.getString(R.string.info_code_10046);
            case 10047: return ctx.getString(R.string.info_code_10047);
            case 10048: return ctx.getString(R.string.info_code_10048);
            case 10049: return ctx.getString(R.string.info_code_10049);
            case 10050: return ctx.getString(R.string.info_code_10050);
            case 10051: return ctx.getString(R.string.info_code_10051);
            case 10052: return ctx.getString(R.string.info_code_10052);
            case 10053: return ctx.getString(R.string.info_code_10053);
            case 10054: return ctx.getString(R.string.info_code_10054);
            case 10055: return ctx.getString(R.string.info_code_10055);
            case 10056: return ctx.getString(R.string.info_code_10056);
            case 10057: return ctx.getString(R.string.info_code_10057);
            case 10058: return ctx.getString(R.string.info_code_10058);
            case 10059: return ctx.getString(R.string.info_code_10059);
            case 10060: return ctx.getString(R.string.info_code_10060);
            case 10061: return ctx.getString(R.string.info_code_10061);
            case 10062: return ctx.getString(R.string.info_code_10062);
            case 10063: return ctx.getString(R.string.info_code_10063);
            case 10064: return ctx.getString(R.string.info_code_10064);
            case 10065: return ctx.getString(R.string.info_code_10065);
            case 10066: return ctx.getString(R.string.info_code_10066);
            case 10067: return ctx.getString(R.string.info_code_10067);
            case 10068: return ctx.getString(R.string.info_code_10068);
            case 10069: return ctx.getString(R.string.info_code_10069);
            case 10070: return ctx.getString(R.string.info_code_10070);
            case 10071: return ctx.getString(R.string.info_code_10071);
            case 10072: return ctx.getString(R.string.info_code_10072);
            case 10073: return ctx.getString(R.string.info_code_10073);
            case 10074: return ctx.getString(R.string.info_code_10074);
            case 10075: return ctx.getString(R.string.info_code_10075);
            case 10076: return ctx.getString(R.string.info_code_10076);
            case 10077: return ctx.getString(R.string.info_code_10077);
            case 10078: return ctx.getString(R.string.info_code_10078);
            case 10079: return ctx.getString(R.string.info_code_10079);
            case 10080: return ctx.getString(R.string.info_code_10080);
            case 10081: return ctx.getString(R.string.info_code_10081);
            case 10082: return ctx.getString(R.string.info_code_10082);
            case 10083: return ctx.getString(R.string.info_code_10083);
            case 10084: return ctx.getString(R.string.info_code_10084);
            case 10085: return ctx.getString(R.string.info_code_10085);
            case 10086: return ctx.getString(R.string.info_code_10086);
            case 10087: return ctx.getString(R.string.info_code_10087);
            case 10088: return ctx.getString(R.string.info_code_10088);
            case 10089: return ctx.getString(R.string.info_code_10089);
            case 10090: return ctx.getString(R.string.info_code_10090);
            case 10091: return ctx.getString(R.string.info_code_10091);
            case 10092: return ctx.getString(R.string.info_code_10092);
            case 10093: return ctx.getString(R.string.info_code_10093);
            case 10094: return ctx.getString(R.string.info_code_10094);
            case 10095: return ctx.getString(R.string.info_code_10095);
            case 10096: return ctx.getString(R.string.info_code_10096);
            case 10097: return ctx.getString(R.string.info_code_10097);
            case 10098: return ctx.getString(R.string.info_code_10098);
            case 10099: return ctx.getString(R.string.info_code_10099);
            case 10100: return ctx.getString(R.string.info_code_10100);
            case 10101: return ctx.getString(R.string.info_code_10101);
            case 10102: return ctx.getString(R.string.info_code_10102);
            case 10103: return ctx.getString(R.string.info_code_10103);
            case 10104: return ctx.getString(R.string.info_code_10104);
            case 10105: return ctx.getString(R.string.info_code_10105);
            case 10106: return ctx.getString(R.string.info_code_10106);
            case 10107: return ctx.getString(R.string.info_code_10107);
            case 10108: return ctx.getString(R.string.info_code_10108);
            case 10109: return ctx.getString(R.string.info_code_10109);
            case 10110: return ctx.getString(R.string.info_code_10110);
            case 10111: return ctx.getString(R.string.info_code_10111);
            case 10112: return ctx.getString(R.string.info_code_10112);
            case 10113: return ctx.getString(R.string.info_code_10113);
            case 10114: return ctx.getString(R.string.info_code_10114);
            case 10115: return ctx.getString(R.string.info_code_10115);
            case 10116: return ctx.getString(R.string.info_code_10116);
            case 10117: return ctx.getString(R.string.info_code_10117);
            case 10118: return ctx.getString(R.string.info_code_10118);
            case 10119: return ctx.getString(R.string.info_code_10119);
            case 10120: return ctx.getString(R.string.info_code_10120);
            case 10121: return ctx.getString(R.string.info_code_10121);
            case 10122: return ctx.getString(R.string.info_code_10122);
            case 10123: return ctx.getString(R.string.info_code_10123);
            case 10124: return ctx.getString(R.string.info_code_10124);
            case 10125: return ctx.getString(R.string.info_code_10125);
            case 10126: return ctx.getString(R.string.info_code_10126);
            case 10127: return ctx.getString(R.string.info_code_10127);
            case 10128: return ctx.getString(R.string.info_code_10128);
            case 10129: return ctx.getString(R.string.info_code_10129);
            case 10130: return ctx.getString(R.string.info_code_10130);
            case 10131: return ctx.getString(R.string.info_code_10131);
            case 10132: return ctx.getString(R.string.info_code_10132);
            case 10133: return ctx.getString(R.string.info_code_10133);
            case 10134: return ctx.getString(R.string.info_code_10134);
            case 10135: return ctx.getString(R.string.info_code_10135);
            case 10136: return ctx.getString(R.string.info_code_10136);
            case 10137: return ctx.getString(R.string.info_code_10137);
            case 10138: return ctx.getString(R.string.info_code_10138);
            case 10139: return ctx.getString(R.string.info_code_10139);
            case 10140: return ctx.getString(R.string.info_code_10140);
            case 10141: return ctx.getString(R.string.info_code_10141);
            case 10142: return ctx.getString(R.string.info_code_10142);
            case 10143: return ctx.getString(R.string.info_code_10143);
            case 10144: return ctx.getString(R.string.info_code_10144);
            case 10145: return ctx.getString(R.string.info_code_10145);
            case 10146: return ctx.getString(R.string.info_code_10146);
            case 10147: return ctx.getString(R.string.info_code_10147);
            case 10148: return ctx.getString(R.string.info_code_10148);
            case 10149: return ctx.getString(R.string.info_code_10149);
            case 10150: return ctx.getString(R.string.info_code_10150);
            case 10151: return ctx.getString(R.string.info_code_10151);
            case 10152: return ctx.getString(R.string.info_code_10152);
            case 10153: return ctx.getString(R.string.info_code_10153);
            case 10154: return ctx.getString(R.string.info_code_10154);
            case 10155: return ctx.getString(R.string.info_code_10155);
            case 10156: return ctx.getString(R.string.info_code_10156);
            case 10157: return ctx.getString(R.string.info_code_10157);
            case 10158: return ctx.getString(R.string.info_code_10158);
            case 10159: return ctx.getString(R.string.info_code_10159);
            case 10160: return ctx.getString(R.string.info_code_10160);
            case 10161: return ctx.getString(R.string.info_code_10161);
            case 10162: return ctx.getString(R.string.info_code_10162);
            case 10163: return ctx.getString(R.string.info_code_10163);
            case 10164: return ctx.getString(R.string.info_code_10164);
            case 10165: return ctx.getString(R.string.info_code_10165);
            case 10166: return ctx.getString(R.string.info_code_10166);
            case 10167: return ctx.getString(R.string.info_code_10167);
            case 10168: return ctx.getString(R.string.info_code_10168);
            case 10169: return ctx.getString(R.string.info_code_10169);
            case 10170: return ctx.getString(R.string.info_code_10170);
            case 10171: return ctx.getString(R.string.info_code_10171);
            case 10172: return ctx.getString(R.string.info_code_10172);
            case 10173: return ctx.getString(R.string.info_code_10173);
            case 10174: return ctx.getString(R.string.info_code_10174);
            case 10175: return ctx.getString(R.string.info_code_10175);
            case 10176: return ctx.getString(R.string.info_code_10176);
            case 10177: return ctx.getString(R.string.info_code_10177);
            case 10178: return ctx.getString(R.string.info_code_10178);
            case 10179: return ctx.getString(R.string.info_code_10179);
            case 10180: return ctx.getString(R.string.info_code_10180);
            case 10181: return ctx.getString(R.string.info_code_10181);
            case 10182: return ctx.getString(R.string.info_code_10182);
            case 10183: return ctx.getString(R.string.info_code_10183);
            case 10184: return ctx.getString(R.string.info_code_10184);
            case 10185: return ctx.getString(R.string.info_code_10185);
            case 10186: return ctx.getString(R.string.info_code_10186);
            case 10187: return ctx.getString(R.string.info_code_10187);
            case 10188: return ctx.getString(R.string.info_code_10188);
            case 10189: return ctx.getString(R.string.info_code_10189);
            case 10190: return ctx.getString(R.string.info_code_10190);
            case 10191: return ctx.getString(R.string.info_code_10191);
            case 10192: return ctx.getString(R.string.info_code_10192);
            case 10193: return ctx.getString(R.string.info_code_10193);
            case 10194: return ctx.getString(R.string.info_code_10194);
            case 10195: return ctx.getString(R.string.info_code_10195);
            case 10196: return ctx.getString(R.string.info_code_10196);
            case 10197: return ctx.getString(R.string.info_code_10197);
            case 10198: return ctx.getString(R.string.info_code_10198);
            case 10199: return ctx.getString(R.string.info_code_10199);
            case 10200: return ctx.getString(R.string.info_code_10200);
            case 10201: return ctx.getString(R.string.info_code_10201);
            case 10202: return ctx.getString(R.string.info_code_10202);
            case 10203: return ctx.getString(R.string.info_code_10203);
            case 10204: return ctx.getString(R.string.info_code_10204);
            case 10205: return ctx.getString(R.string.info_code_10205);
            case 10206: return ctx.getString(R.string.info_code_10206);
            case 10207: return ctx.getString(R.string.info_code_10207);
            case 10208: return ctx.getString(R.string.info_code_10208);
            case 10209: return ctx.getString(R.string.info_code_10209);
            case 10210: return ctx.getString(R.string.info_code_10210);
            case 10211: return ctx.getString(R.string.info_code_10211);
            case 10212: return ctx.getString(R.string.info_code_10212);
            case 10213: return ctx.getString(R.string.info_code_10213);
            case 10214: return ctx.getString(R.string.info_code_10214);
            case 10215: return ctx.getString(R.string.info_code_10215);
            case 10216: return ctx.getString(R.string.info_code_10216);
            case 10217: return ctx.getString(R.string.info_code_10217);
            case 10218: return ctx.getString(R.string.info_code_10218);
            case 10219: return ctx.getString(R.string.info_code_10219);
            case 10220: return ctx.getString(R.string.info_code_10220);
            case 10221: return ctx.getString(R.string.info_code_10221);
            case 10222: return ctx.getString(R.string.info_code_10221);
            case 10223: return ctx.getString(R.string.info_code_10222);
            case 10224: return ctx.getString(R.string.info_code_10223);
            case 10225: return ctx.getString(R.string.info_code_10224);
            case 10226: return ctx.getString(R.string.info_code_10225);
            case 10227: return ctx.getString(R.string.info_code_10226);
            case 10228: return ctx.getString(R.string.info_code_10227);
            case 10229: return ctx.getString(R.string.info_code_10228);
            case 10230: return ctx.getString(R.string.info_code_10229);
            case 10231: return ctx.getString(R.string.info_code_10230);
            case 10232: return ctx.getString(R.string.info_code_10231);
            case 10233: return ctx.getString(R.string.info_code_10232);
            case 10234: return ctx.getString(R.string.info_code_10234);
            case 10235: return ctx.getString(R.string.info_code_10235);
            case 10236: return ctx.getString(R.string.info_code_10236);
            case 10237: return ctx.getString(R.string.info_code_10237);
            case 10238: return ctx.getString(R.string.info_code_10238);
            case 10239: return ctx.getString(R.string.info_code_10239);
            case 10240: return ctx.getString(R.string.info_code_10240);
            case 10241: return ctx.getString(R.string.info_code_10241);
            case 10242: return ctx.getString(R.string.info_code_10242);
            case 10243: return ctx.getString(R.string.info_code_10243);
            case 10244: return ctx.getString(R.string.info_code_10244);
            case 10245: return ctx.getString(R.string.info_code_10245);
            case 10246: return ctx.getString(R.string.info_code_10246);
            case 10247: return ctx.getString(R.string.info_code_10247);
            case 10250: return ctx.getString(R.string.info_code_10250);
            case 10251: return ctx.getString(R.string.info_code_10251);
            case 10252: return ctx.getString(R.string.info_code_10252);

            // 音乐类型
            case 21000: return ctx.getString(R.string.info_code_21000);
            case 21001: return ctx.getString(R.string.info_code_21001);
            case 21002: return ctx.getString(R.string.info_code_21002);
            case 21003: return ctx.getString(R.string.info_code_21003);
            case 21004: return ctx.getString(R.string.info_code_21004);
            case 21005: return ctx.getString(R.string.info_code_21005);
            case 21006: return ctx.getString(R.string.info_code_21006);
            case 21007: return ctx.getString(R.string.info_code_21007);
            case 21008: return ctx.getString(R.string.info_code_21008);
            case 21009: return ctx.getString(R.string.info_code_21009);
            case 21010: return ctx.getString(R.string.info_code_21010);
            case 21011: return ctx.getString(R.string.info_code_21011);
            case 21012: return ctx.getString(R.string.info_code_21012);
            case 21013: return ctx.getString(R.string.info_code_21013);
            case 21014: return ctx.getString(R.string.info_code_21014);
            case 21015: return ctx.getString(R.string.info_code_21015);
            case 21016: return ctx.getString(R.string.info_code_21016);
            case 21017: return ctx.getString(R.string.info_code_21017);
            case 21018: return ctx.getString(R.string.info_code_21018);
            case 21019: return ctx.getString(R.string.info_code_21019);

            // 影片类型
            case 22000: return ctx.getString(R.string.info_code_22000);
            case 22001: return ctx.getString(R.string.info_code_22001);
            case 22002: return ctx.getString(R.string.info_code_22002);
            case 22003: return ctx.getString(R.string.info_code_22003);
            case 22004: return ctx.getString(R.string.info_code_22004);
            case 22005: return ctx.getString(R.string.info_code_22005);
            case 22006: return ctx.getString(R.string.info_code_22006);
            case 22007: return ctx.getString(R.string.info_code_22007);
            case 22008: return ctx.getString(R.string.info_code_22008);
            case 22009: return ctx.getString(R.string.info_code_22009);
            case 22010: return ctx.getString(R.string.info_code_22010);
            case 22011: return ctx.getString(R.string.info_code_22011);
            case 22012: return ctx.getString(R.string.info_code_22012);
            case 22013: return ctx.getString(R.string.info_code_22013);
            case 22014: return ctx.getString(R.string.info_code_22014);
            case 22015: return ctx.getString(R.string.info_code_22015);
            case 22016: return ctx.getString(R.string.info_code_22016);
            case 22017: return ctx.getString(R.string.info_code_22017);
            case 22018: return ctx.getString(R.string.info_code_22018);
            case 22019: return ctx.getString(R.string.info_code_22019);
            case 22020: return ctx.getString(R.string.info_code_22020);
            case 22021: return ctx.getString(R.string.info_code_22021);
            case 22022: return ctx.getString(R.string.info_code_22022);

            //举报
            case 23000: return ctx.getString(R.string.report_type_23000);
            case 23001: return ctx.getString(R.string.report_type_23001);
            case 23002: return ctx.getString(R.string.report_type_23002);
            case 23003: return ctx.getString(R.string.report_type_23003);
            case 23004: return ctx.getString(R.string.report_type_23004);
            case 23005: return ctx.getString(R.string.report_type_23005);
            case 23006: return ctx.getString(R.string.report_type_23006);
            case 23007: return ctx.getString(R.string.report_type_23007);
            case 23008: return ctx.getString(R.string.report_type_23008);
            case 23009: return ctx.getString(R.string.report_type_23009);
            case 23010: return ctx.getString(R.string.report_type_23010);
            case 23011: return ctx.getString(R.string.report_type_23011);
            case 23012: return ctx.getString(R.string.report_type_23012);
            case 23013: return ctx.getString(R.string.report_type_23013);
            case 23014: return ctx.getString(R.string.report_type_23014);
            case 23015: return ctx.getString(R.string.report_type_23015);

            case 23018: return ctx.getString(R.string.info_code_23018);
            case 24000: return ctx.getString(R.string.info_code_24000);
            case 24001: return ctx.getString(R.string.info_code_24001);
            case 24002: return ctx.getString(R.string.info_code_24002);
            case 24003: return ctx.getString(R.string.info_code_24003);
            default:
                return null;
        }
    }
}
