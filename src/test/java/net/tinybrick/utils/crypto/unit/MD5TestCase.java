package net.tinybrick.utils.crypto.unit;

/**
 * Created by wangji on 2016/6/12.
 */
import net.tinybrick.utils.crypto.MD5;
import org.junit.Assert;
import org.junit.Test;

public class MD5TestCase {
    @Test
    public void MD5UpdateTest() {
        MD5 md5 = new MD5();
        md5.update("A".getBytes());
        md5.update("B".getBytes());
        String hash = md5.digest();

        Assert.assertEquals(MD5.hash("AB"), hash);
    }
}
