package at.lmk.webapp.elements;

import java.lang.reflect.Field;

public class ReflectedTableColumn implements TableColumn {

	private String title;
	private String fieldName;

	public ReflectedTableColumn(String title, String fieldName) {
		this.title = title;
		this.fieldName = fieldName;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getContent(Object o) {
		Object content = "";
		try {
			Field field = o.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			content = field.get(o);
			field.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return content.toString();
	}

}
