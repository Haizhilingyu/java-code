public class YamlFileRead{

    public Map<String,Object> readYaml(String filePath){
         Yaml yaml = new Yaml();
        //如果读入Map,这里可以是Mapj接口,默认实现为LinkedHashMap
        CollectionDataConfig dataConfig = null;
        try {
            return yaml.loadAs(new FileInputStream(new File(filePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}