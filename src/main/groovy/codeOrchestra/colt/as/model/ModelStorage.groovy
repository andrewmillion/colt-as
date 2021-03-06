package codeOrchestra.colt.as.model
/**
 * @author Dima Kruk
 */
class ModelStorage {
    private static ModelStorage ourInstance = new ModelStorage()

    public static ModelStorage getInstance() {
        return ourInstance
    }

    AsProject project

    private ModelStorage() {
        project = new AsProject()
    }

    public String save() {
        return project.toXmlString()
    }

    public void load(String xmlStr) {
        project.fromXmlString(xmlStr)
    }

}
