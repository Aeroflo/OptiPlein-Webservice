package com.api.optiplein;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
class OptipleinApplicationTests {

	@Test
	void parseInt(){

		String v = null;
		String str = ".9500";
		BigDecimal b = new BigDecimal(str);
		BigDecimal b1 = new BigDecimal("test");
		BigDecimal b2 = new BigDecimal(v);


	}

	@Test
	void contextLoads() {
	}

	@Test
	void testXMLMapping() throws JsonProcessingException {

		String xml = "<pdv id=\"2100017\" latitude=\"4983290\" longitude=\"330705\" cp=\"02100\" pop=\"R\">"
				+ "<adresse>Rue de la Fère</adresse>"
    			+ "<ville>Saint-Quentin</ville>"
    			+ "<services>   <service>Boutique alimentaire</service>      <service>DAB (Distributeur automatique de billets)</service>     <service>Lavage automatique</service>"
				+ " </services>    " +
				"<prix nom=\"SP98\" id=\"6\" maj=\"2021-02-22T09:18:04\" valeur=\"1450\"/>"
				+ "<prix nom=\"Gazole\" id=\"1\" maj=\"2021-05-10T11:38:50\" valeur=\"1362\"/>\n" +
				"    <prix nom=\"SP95\" id=\"2\" maj=\"2021-05-10T11:38:50\" valeur=\"1520\"/>\n" +
				"    <prix nom=\"E10\" id=\"5\" maj=\"2021-05-10T11:38:51\" valeur=\"1480\"/>"
				+ " <rupture id=\"1\" nom=\"Gazole\" debut=\"2021-03-24T13:06:06\" fin=\"\"/>"
				+ "<rupture id=\"2\" nom=\"SP95\" debut=\"2021-03-24T13:06:07\" fin=\"\"/>"
				+ "  </pdv>";

		ObjectWriter w = new ObjectMapper().writerWithDefaultPrettyPrinter();
		Object o;


		o = new XmlMapper()
				.readValue(xml, Object.class);
		System.out.println( w.writeValueAsString(o) );

		o = new XmlMapper()
				.registerModule(new SimpleModule().addDeserializer(Object.class, new FixedUntypedObjectDeserializer()))
				.readValue(xml, Object.class);
		System.out.println( w.writeValueAsString(o) );

		}

	}

 class FixedUntypedObjectDeserializer extends UntypedObjectDeserializer {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
		String firstKey;

		JsonToken t = p.getCurrentToken();

		if (t == JsonToken.START_OBJECT) {
			firstKey = p.nextFieldName();
		} else if (t == JsonToken.FIELD_NAME) {
			firstKey = p.getCurrentName();
		} else {
			if (t != JsonToken.END_OBJECT) {
				throw ctxt.mappingException(handledType(), p.getCurrentToken());
			}
			firstKey = null;
		}

		// empty map might work; but caller may want to modify... so better
		// just give small modifiable
		LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(2);
		if (firstKey == null)
			return resultMap;

		p.nextToken();
		resultMap.put(firstKey, deserialize(p, ctxt));

		// 03-Aug-2016, jpvarandas: handle next objects and create an array
		Set<String> listKeys = new LinkedHashSet<>();

		String nextKey;
		while ((nextKey = p.nextFieldName()) != null) {
			p.nextToken();
			if (resultMap.containsKey(nextKey)) {
				Object listObject = resultMap.get(nextKey);

				if (!(listObject instanceof List)) {
					listObject = new ArrayList<>();
					((List) listObject).add(resultMap.get(nextKey));

					resultMap.put(nextKey, listObject);
				}

				((List) listObject).add(deserialize(p, ctxt));

				listKeys.add(nextKey);
			}
			else if(nextKey.equals("prix")){
				Object listObject = resultMap.get(nextKey);
				if(listObject == null){
					listObject = new ArrayList<>();
					resultMap.put(nextKey, listObject);
				}else{
					((List) listObject).add(resultMap.get(nextKey));
				}
				/*if (!(listObject instanceof List)) {
					listObject = new ArrayList<>();
					((List) listObject).add(resultMap.get(nextKey));

					resultMap.put(nextKey, listObject);
				}*/

				((List) listObject).add(deserialize(p, ctxt));

				listKeys.add(nextKey);
			}
			else {
				resultMap.put(nextKey, deserialize(p, ctxt));

			}
		}

		return resultMap;

	}

}

