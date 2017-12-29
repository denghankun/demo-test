package com.dhkun.test.mybatis;

import org.mybatis.generator.api.ShellRunner;

public class MybatisGeneratorSample {

	public static void main(String[] args) {
		args = new String[] {"-configfile", "src/main/resources/generatorConfig.xml", "-overwrite"};
		//args = new String[]{"-configfile", "src/main/resources/generatorConfig2.xml", "-overwrite"};
		ShellRunner.main(args);
	}
}
