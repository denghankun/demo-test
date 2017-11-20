package com.dhkun.test.mybatis.plugin;

import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import com.dhkun.test.mybatis.constants.CodeGenConstants;
import com.dhkun.test.mybatis.constants.DefaultField;

public class InsertBatchElementGenerator extends
		AbstractXmlElementGenerator {

	private static final List<DefaultField> INSERTBATCH_FIELDS = Arrays.asList(DefaultField.DISABLED, DefaultField.CREATED, DefaultField.UPDATED);
	
	private int columnsContains; // 0包含所有列;1除主键外所有列
	
	public InsertBatchElementGenerator() {
		super();
		this.columnsContains = CodeGenConstants.InsertBatchColumns.ALL_COLUMN;
	}
	
	public InsertBatchElementGenerator(int columnsContains) {
		super();
		this.columnsContains = columnsContains;
	}
	
	@Override
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("insert");
        answer.addAttribute(new Attribute("id", "insertBatch")); //$NON-NLS-1$
        answer.addAttribute(new Attribute("parameterType", "java.util.List"));


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

        answer.addElement(new TextElement("values"));
        
        XmlElement forEachElement = new XmlElement("foreach");
        forEachElement.addAttribute(new Attribute("collection", "list"));
        forEachElement.addAttribute(new Attribute("item", "item"));
        forEachElement.addAttribute(new Attribute("index", "index"));
        forEachElement.addAttribute(new Attribute("separator", ","));
        answer.addElement(forEachElement);
        
        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        forEachElement.addElement(valuesTrimElement);

        List<IntrospectedColumn> introspectedColumns = null;
        if (columnsContains == CodeGenConstants.InsertBatchColumns.ALL_COLUMN) {
        	introspectedColumns = introspectedTable.getAllColumns();
		} else {
			introspectedColumns = introspectedTable.getNonPrimaryKeyColumns();
		}
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
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
            if (containsDefaultField(columnName, INSERTBATCH_FIELDS)) {
                addInsertDefautlField(columnName,insertTrimElement, valuesTrimElement, INSERTBATCH_FIELDS);
            }  else {

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));
            }
        }
        
        System.out.println("新增insertBatch脚本为:\n" + answer.getFormattedContent(0));
        
        parentElement.addElement(answer); // 添加到父节点中
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

}
