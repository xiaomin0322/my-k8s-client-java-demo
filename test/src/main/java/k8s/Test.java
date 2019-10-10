package k8s;

import java.io.InputStream;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

public class Test {

	public static void main(String[] args) throws Exception {
		String fileName = "classpath:/k8s/config";
		InputStream inputStream = ResourceRenderer.resourceLoader(fileName);
		ApiClient client = Config.fromConfig(inputStream);
		// 五分钟
		client.setConnectTimeout(5 * 60 * 1000);
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		for (V1Pod item : list.getItems()) {
			System.out.println(item.getMetadata().getName());
		}
		
		V1NodeList listNode = api.listNode(null, null, null, null, null, null, null, null, null);
		for (V1Node item : listNode.getItems()) {
			System.out.println(item.getMetadata().getName());
		}
	}
}
