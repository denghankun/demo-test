package com.dhkun.test.mybatis.plugin;

import java.util.Arrays;
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
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

import com.dhkun.test.mybatis.constants.CodeGenConstants;
import com.dhkun.test.mybatis.constants.DefaultField;

public class PaginationPlugin extends PluginAdapter {
    private static final List<DefaultField> INSERTSELECTIVE_FIELDS = Arrays.asList(DefaultField.DISABLED, DefaultField.CREATED, DefaultField.UPDATED);
    /**
     * 更新操作忽略'created'字段
     */
    private static final List<DefaultField> UPDATESELECTIVE_FIELDS = Arrays.asList(DefaultField.DISABLED, DefaultField.CREATED_INGORED, DefaultField.UPDATED);

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
		interfaze.getMethods().clear();// 清空默认添加的方法
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
		
		// 增加批量插入脚本
		InsertBatchElementGenerator insertBatchGen = new InsertBatchElementGenerator(CodeGenConstants.InsertBatchColumns.NON_PK_COLUMN);// 默认不包含主键
		insertBatchGen.setIntrospectedTable(introspectedTable); // 进行初始化
		insertBatchGen.addElements(parentElement); // 新增脚本

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

	@Override
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
												  IntrospectedTable introspectedTable) {
		//System.out.println("sqlMapInsertSelective=" + element.getFormattedContent(0));
		// 修改默认insertSelective的实现
		// 修改1：名字从insertSelective改为insert
		// 修改2：如果含有字段disable，created，updated，赋予默认值
		element.getAttributes().clear(); // 清空原有所有属性
		element.getElements().clear(); // 清空原有所有元素

        XmlElement answer = element;
        answer.addAttribute(new Attribute("id", introspectedTable.getInsertSelectiveStatementId())); //$NON-NLS-1$
        //element.addAttribute(new Attribute("id", "insert"));

        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));


        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                    answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("insert into "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        insertTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(insertTrimElement);

        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(valuesTrimElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isIdentity()) {
                // cannot set values on identity fields
                continue;
            }

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            if (containsDefaultField(columnName, INSERTSELECTIVE_FIELDS)) {
                addInsertDefautlField(columnName,insertTrimElement, valuesTrimElement, INSERTSELECTIVE_FIELDS);
            }  else {
                XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                sb.setLength(0);
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                insertNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertNotNullElement.addElement(new TextElement(sb.toString()));
                insertTrimElement.addElement(insertNotNullElement);

                XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                sb.setLength(0);
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                valuesNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesNotNullElement.addElement(new TextElement(sb.toString()));
                valuesTrimElement.addElement(valuesNotNullElement);
            }
        }

        System.out.println("新输出的insertSelective语句为：\n" + answer.getFormattedContent(0));
        return super.sqlMapInsertSelectiveElementGenerated(answer, introspectedTable);
	}
	
	@Override
	public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
		element.getAttributes().clear(); // 清空原有所有属性
        element.getElements().clear(); // 清空原有所有元素
		
		XmlElement answer = element;
		answer.addAttribute(new Attribute("id", introspectedTable.getUpdateByPrimaryKeySelectiveStatementId())); //$NON-NLS-1$
		
		String parameterType;
		
		if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
		    parameterType = introspectedTable.getRecordWithBLOBsType();
		} else {
		    parameterType = introspectedTable.getBaseRecordType();
		}
		
		answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
		        parameterType));
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("update "); //$NON-NLS-1$
		sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
		answer.addElement(new TextElement(sb.toString()));
		
		XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
		answer.addElement(dynamicElement);
		
		for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
			String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
			if (containsDefaultField(columnName, UPDATESELECTIVE_FIELDS)) {
				addUpdateDefaultField(columnName, dynamicElement, UPDATESELECTIVE_FIELDS);
			} else {
			    XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			    sb.setLength(0);
			    sb.append(introspectedColumn.getJavaProperty());
			    sb.append(" != null"); //$NON-NLS-1$
			    isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
			    dynamicElement.addElement(isNotNullElement);
			
			    sb.setLength(0);
			    sb.append(columnName);
			    sb.append(" = "); //$NON-NLS-1$
			    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
			    sb.append(',');
			
			    isNotNullElement.addElement(new TextElement(sb.toString()));
			}
		}
		
		boolean and = false;
		for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
		    sb.setLength(0);
		    if (and) {
		        sb.append("  and "); //$NON-NLS-1$
		    } else {
		        sb.append("where "); //$NON-NLS-1$
		        and = true;
		    }
		
		    sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
		    sb.append(" = "); //$NON-NLS-1$
		    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
		    answer.addElement(new TextElement(sb.toString()));
		}
		System.out.println("新输出的updateSelective语句为：\n" + answer.getFormattedContent(0));
		return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer, introspectedTable);
	}

	public boolean validate(List<String> arg0) {
		return true;
	}

	public static void generate() {
		String config = PaginationPlugin.class.getClassLoader().getResource("mybatisConfig.xml").getFile();
		String[] arg = { "-configfile", config };
		ShellRunner.main(arg);
	}

	public static void main(String[] args) {
		generate();
	}

    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn,
                                      GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", identityColumnType)); //$NON-NLS-1$
        answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
        answer.addAttribute(new Attribute("order", //$NON-NLS-1$
                generatedKey.getMyBatis3Order()));

        answer.addElement(new TextElement(generatedKey.getRuntimeSqlStatement()));

        return answer;
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

    private boolean containsDefaultField(String columnName, List<DefaultField> defaultFields) {
    	for (DefaultField defaultField : defaultFields) {
    		if (defaultField.getField().equals(columnName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private void addInsertDefautlField(String columnName, XmlElement insertTrimElement, XmlElement valuesTrimElement, List<DefaultField> defaultFields) {
        StringBuilder sb = new StringBuilder();
        for (DefaultField defaultField : defaultFields) {
			if (defaultField.getField().equals(columnName)
					&& defaultField.getDefaultValue() != null) {
				sb.setLength(0);
	            sb.append(columnName).append(',');
	            insertTrimElement.addElement(new TextElement(sb.toString()));

	            sb.setLength(0);
	            sb.append(defaultField.getDefaultValue()).append(',');
	            valuesTrimElement.addElement(new TextElement(sb.toString()));
			}
		}
    }
    
    private void addUpdateDefaultField(String columnName, XmlElement dynamicElement, List<DefaultField> defaultFields) {
    	StringBuilder sb = new StringBuilder();
    	for (DefaultField defaultField : defaultFields) {
    		if (defaultField.getField().equals(columnName)
    				&& defaultField.getDefaultValue() != null) {
	    		sb.setLength(0);
			    sb.append(columnName);
			    sb.append(" = "); //$NON-NLS-1$
			    sb.append(defaultField.getDefaultValue());
			    sb.append(',');
			    dynamicElement.addElement(new TextElement(sb.toString()));
    		}
		}
    }
}
