package com.kii.iotcloud.exception;

import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.SmallTestBase;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IoTCloudRestExceptionTest extends SmallTestBase {
    @Test
    public void httpStatusTest() throws Exception {
        Assert.assertEquals(400, new BadRequestException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(401, new UnauthorizedException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(403, new ForbiddenException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(404, new NotFoundException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(409, new ConflictException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(500, new InternalServerErrorException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(503, new ServiceUnavailableException("", new JSONObject()).getStatusCode());
        Assert.assertEquals(504, new GatewayTimeoutException("", new JSONObject()).getStatusCode());
    }
    @Test
    public void errorCodeTest() throws Exception {
        JSONObject responseBody = new JSONObject();
        responseBody.put("errorCode", "OWNER_NOT_FOUND");
        Assert.assertEquals(IoTCloudErrorCode.OWNER_NOT_FOUND, new IoTCloudRestException("", 404, responseBody).getErrorCode());
        Assert.assertEquals("OWNER_NOT_FOUND", new IoTCloudRestException("", 404, responseBody).getErrorCode().getCode());
    }
    @Test
    public void unknownErrorCodeTest() throws Exception {
        JSONObject responseBody = new JSONObject();
        responseBody.put("errorCode", "__OWNER_NOT_FOUND__");
        Assert.assertEquals(new UnkownErrorCode("__OWNER_NOT_FOUND__"), new IoTCloudRestException("", 404, responseBody).getErrorCode());
        Assert.assertEquals("__OWNER_NOT_FOUND__", new IoTCloudRestException("", 404, responseBody).getErrorCode().getCode());
    }
}
