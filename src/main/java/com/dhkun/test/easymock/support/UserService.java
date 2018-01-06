package com.dhkun.test.easymock.support;

import java.util.List;

public interface UserService {

    List<String> queryNames(String[] ids) throws Exception;

}
