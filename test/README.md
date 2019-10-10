kubernates java client 官方包地址：
https://github.com/kubernetes-client/java
1、根据官方文档引入pom
2、登陆kubernates集群master节点找到kubectl.kubeconfig文件
3、将kubectl.kubeconfig放到代码resources相应的位置
Spring boot 读取jar包中resources下的文件

 String fileName = "classpath:/k8s/kubectl.kubeconfig";
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
