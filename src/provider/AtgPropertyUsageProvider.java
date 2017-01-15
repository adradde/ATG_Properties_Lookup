package provider;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.impl.PropertiesFileImpl;
import com.intellij.psi.PsiFile;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.asMap;
import static java.util.Arrays.asList;

/**
 * Created by andrii on 14.01.2017.
 */
public class AtgPropertyUsageProvider extends com.intellij.codeInspection.unused.ImplicitPropertyUsageProvider {

	public static final String $_CLASS = "$class";
	private static final String[] reservedWords = { "$class", "$scope" };

	@Override
	protected boolean isUsed(com.intellij.lang.properties.psi.Property property) {

		if (isAtgReservedWord(property))
			return true;

		PsiFile propertyFile = property.getContainingFile();

		if (propertyFile instanceof PropertiesFileImpl) {
			PropertiesFileImpl propertiesFile = ((PropertiesFileImpl) propertyFile);
			Map<String, String> propertiesFileNamesMap = propertiesFile.getNamesMap();

			List<IProperty> keys = propertiesFile.getProperties();
			if (keys.contains($_CLASS)) {
				String classPath = propertiesFile.findPropertyByKey($_CLASS).getValue();
				try {
					Class<?> instanceClass = ClassLoader.getSystemClassLoader().loadClass(classPath);
					Map<Field, String> fieldNameMap = asMap(Sets.newHashSet(instanceClass.getFields()), field -> field.getName());
					filterReservedWords(fieldNameMap);

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//			FileViewProvider viewProvider = propertyFile.getViewProvider();
			//		Document document = viewProvider.getDocument();
			//		document.
		}

		return false;
	}

	private void filterReservedWords(Map<Field, String> fieldNameMap) {
		asList(reservedWords).stream().forEach(word -> fieldNameMap.remove(word));
	}

	private boolean isAtgReservedWord(com.intellij.lang.properties.psi.Property property) {
		return asList(reservedWords).contains(property.getName());
	}
}

