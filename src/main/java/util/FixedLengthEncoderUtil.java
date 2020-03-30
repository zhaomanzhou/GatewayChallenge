package util;


/**
 * 如果使用了粘包处理器，西药用此util度发送消息处理
 *
 *   body长度              body内容
 * ----------------+-------------------------
 *        10位     |
 *-----------------+-------------------------
 */
public class FixedLengthEncoderUtil
{
    public static String encoder(String s)
    {
        String head = generatedHead(s.getBytes().length);
        return head + s;
    }

    public static String generatedHead(int length)
    {
        String headLengthPart = String.format("%10d", length);
        String reserved = String.format("%10s", " ");
        return headLengthPart + reserved;
    }



}