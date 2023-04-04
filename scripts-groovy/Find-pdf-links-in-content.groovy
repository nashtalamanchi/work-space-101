/**
* Groovy Script to get the list of all .pdf Authroed Links in the content.
*
* Note - This pulls the the .pdf links even authroed as html snippet. 
**/
import org.apache.sling.api.resource.ModifiableValueMap
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import java.text.SimpleDateFormat
import com.day.cq.dam.api.AssetManager
import java.text.NumberFormat
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils

String text = '.pdf'
String contentDam = '/content/dam'
String query = "/jcr:root/content/we-retail/en//*[(jcr:contains(., '$text'))]"

resourceResolver = (ResourceResolver) resourceResolver
resourceResolver.findResources(query, 'xpath').forEachRemaining { Resource resource ->
    Page page = pageManager.getContainingPage(resource.getPath())
    if(StringUtils.equalsIgnoreCase(page.getContentResource().getValueMap().get("cq:lastReplicationAction", ""), "Activate")) {
        ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap)

        valueMap.entrySet().forEach { Map.Entry<String, Object> entry ->
            Object value = entry.value
            if (value instanceof String && value.contains(text)) {
                if (value.endsWith(text) && value.contains(contentDam)) {    
                    println(value.substring(value.indexOf(contentDam), value.length()) + "|" + resource.getPath())        
                } else {
                    final Matcher subMatcher = Pattern.compile('href=".*?"').matcher(value)
                    while(subMatcher.find()) {
                        String matchedValue = (String) value.substring(subMatcher.start() +6, subMatcher.end() -1)
                        if (matchedValue.endsWith(text) && matchedValue.contains(contentDam)) {
                            println(matchedValue.substring(matchedValue.indexOf(contentDam), matchedValue.length()) + "|" + resource.getPath())
                        }
                    }
                }
            }
        }
    }
}
