package com.dhkun.test.easymock;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;

import com.dhkun.test.easymock.support.UserService;

public class MockSample {
	
	public static void main(String[] args) throws Exception {
		String[] ids = { "1", "2", "3" };
		List<String> returnNams = Arrays.asList("a,b,c".split(","));
		UserService userServiceMock = EasyMock.createMock(UserService.class);
		//System.out.println(userServiceMock.queryNames(ids));
		// 期望userService.queryNames返回结果为[a, b, c],并且只执行一次
		EasyMock.expect(userServiceMock.queryNames(ids)).andReturn(returnNams).times(1);
		EasyMock.replay(userServiceMock);
		System.out.println(userServiceMock.queryNames(ids));
		EasyMock.verify(userServiceMock);
	}
}
