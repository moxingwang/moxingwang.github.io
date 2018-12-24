package com.chinaredstar.ordercenter.test.http.afterservice;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;

import com.chinaredstar.ordercenter.module.enums.AttachmentType;
import com.chinaredstar.ordercenter.module.enums.CustomerServiceType;
import com.chinaredstar.ordercenter.module.enums.FileType;
import com.chinaredstar.ordercenter.module.enums.ReturnType;
import com.chinaredstar.ordercenter.util.HttpUtils;
import com.chinaredstar.ordercenter.vo.createcustomerservice.CreateAttachmentVo;
import com.chinaredstar.ordercenter.vo.createcustomerservice.CreateRefoundOrderItemVo;
import com.chinaredstar.ordercenter.vo.createcustomerservice.UpdateCustomerServiceVo;
import com.chinaredstar.ordercenter.vo.pricedifference.OrderCheckVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import springfox.documentation.spring.web.json.Json;

public class ReapplyCustomerServiceTest {
	
private String url = "http://localhost:8080/p-trade-oc-web/orderApi/cApp/updateCustomerService";
	
	private static Map<String, String> headers = new HashMap<String, String>();
	
	static {
		headers.put("x-auth-token", "9ec7afe2-5e75-48a3-aa5a-27f1a37b8f77");
		headers.put("Content-Type", "application/json");
	}
	
	@Test
	public void test() {
		
		UpdateCustomerServiceVo request = new UpdateCustomerServiceVo();
		request.setId((long) 33);
		request.setChannelId((long) 1);
		request.setPlantformId((long) 3);
		request.setClientId((long) 4);
		request.setOrderId((long) 268156);
		request.setCustomerServiceType(CustomerServiceType.REFUND);
		CreateAttachmentVo attachmentVo = new CreateAttachmentVo();
		attachmentVo.setAttachmentType(AttachmentType.CUSTOMER_SERVICE);
		attachmentVo.setFileType(FileType.JPEG);
		attachmentVo.setAttachmentUrl("http://123.123");
		request.getAttachments().add(attachmentVo);
		
		CreateRefoundOrderItemVo createRefoundOrderItem = new CreateRefoundOrderItemVo();
		createRefoundOrderItem.setQuantity(BigDecimal.ONE);
		createRefoundOrderItem.setSku("10226732");
		request.setOrderItem(createRefoundOrderItem);
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter writer = objectMapper.writer();
		
		String jsonPayload = null;
		
		try {
			jsonPayload = JSON.toJSONString(request);
			UpdateCustomerServiceVo a = JSON.parseObject(jsonPayload,UpdateCustomerServiceVo.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String result = HttpUtils.post(url, jsonPayload, Charset.forName("utf8"), headers);
			
		try {
			HashMap object = objectMapper.readValue(result, HashMap.class);
			String code = (String) object.get("code");
			System.out.println(result);
			
			Assert.assertTrue("200".equals(code));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
