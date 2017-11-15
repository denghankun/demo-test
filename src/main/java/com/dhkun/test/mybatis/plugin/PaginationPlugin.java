package com.dhkun.test.mybatis.plugin;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class PaginationPlugin extends PluginAdapter {
	/**
	 * 生成dao
	 */
	@Override
	public boolean clientGenerated(Interface interfaze,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseDao<" + introspectedTable.getBaseRecordType() + ">");
		FullyQualifiedJavaType imp = new FullyQualifiedJavaType("common.BaseDao");
		interfaze.addSuperInterface(fqjt);// 添加 extends BaseDao<User>
		interfaze.addImportedType(imp);// 添加import common.BaseDao;
		interfaze.getMethods().clear();
		return true;
	}

	/**
	 * 生成实体中每个属性
	 */
	@Override
	public boolean modelGetterMethodGenerated(Method method,
			TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return true;
	}

	/**
	 * 生成实体
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		addSerialVersionUID(topLevelClass, introspectedTable);
		return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
	}

	/**
	 * 生成mapping
	 */
	@Override
	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
			IntrospectedTable introspectedTable) {
		return super.sqlMapGenerated(sqlMap, introspectedTable);
	}

	/**
	 * 生成mapping 添加自定义sql
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document,
			IntrospectedTable introspectedTable) {
		String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();// 数据库表名
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		XmlElement parentElement = document.getRootElement();

		// 添加sql——where
		XmlElement sql = new XmlElement("sql");
		sql.addAttribute(new Attribute("id", "sql_where"));
		XmlElement where = new XmlElement("where");
		StringBuilder sb = new StringBuilder();
		for (IntrospectedColumn column : introspectedTable.getNonPrimaryKeyColumns()) {
			XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$  
			sb.setLength(0);
			sb.append(column.getJavaProperty());
			sb.append(" != null"); //$NON-NLS-1$  
			isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$  
			where.addElement(isNotNullElement);

			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(column));
			sb.append(" = "); //$NON-NLS-1$  
			sb.append(MyBatis3FormattingUtilities.getParameterClause(column));
			isNotNullElement.addElement(new TextElement(sb.toString()));
		}
		sql.addElement(where);
		parentElement.addElement(sql);

		// 添加getList
		XmlElement select = new XmlElement("select");
		select.addAttribute(new Attribute("id", "getList"));
		select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
		select.addElement(new TextElement(" select * from " + tableName));

		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_where"));

		select.addElement(include);
		parentElement.addElement(select);

		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {
		// LIMIT5,10; // 检索记录行 6-15
		//      XmlElement isNotNullElement = new XmlElement("if");//$NON-NLS-1$  
		//      isNotNullElement.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));//$NON-NLS-1$ //$NON-NLS-2$  
		// isNotNullElement.addElement(new
		// TextElement("limit ${limitStart} , ${limitEnd}"));
		// element.addElement(isNotNullElement);
		// LIMIT 5;//检索前 5个记录行
		return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,
				introspectedTable);
	}

	private void addSerialVersionUID(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setStatic(true);
		field.setFinal(true);
		field.setName("serialVersionUID");
		field.setInitializationString("1L");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
	}

	public boolean validate(List<String> arg0) {
		return true;
	}

	public static void generate() {
		String config = PaginationPlugin.class.getClassLoader()
				.getResource("mybatisConfig.xml").getFile();
		String[] arg = { "-configfile", config };
		ShellRunner.main(arg);
	}

	public static void main(String[] args) {
		generate();
	}
}
