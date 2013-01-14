package hudson.plugins.testng.results;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestResult;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * Base class that takes care of all the common functionality of the different kinds of
 * test results.
 */
@SuppressWarnings("serial")
@ExportedBean
public abstract class BaseResult extends TabulatedResult implements ModelObject, Serializable {

    //owner of this build
    protected AbstractBuild<?, ?> owner;
    //name of this result
    protected final String name;
    //parent result for this result
    protected BaseResult parent;

    public BaseResult(String name) {
        this.name = name;
    }

    @Exported(visibility = 999)
    @Override
    public String getName() {
        return name;
    }

    @Override
    public BaseResult getParent() {
        return parent;
    }

    public void setParent(BaseResult parent) {
        this.parent = parent;
    }

    @Override
    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public void setOwner(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    public String getDisplayName() {
        return getName();
    }

    public String getUpUrl() {
        return Jenkins.getInstance().getRootUrl() + owner.getUrl() + getId();
    }

    @Override
    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
        for (TestResult result : this.getChildren()) {
            if (token.equals(result.getName())) {
                return result;
            }
        }
        return null;
    }

    @Override
    public TestResult findCorrespondingResult(String id) {
        if (getId().equals(id) || id == null) {
            return this;
        }

        int sepIdx = id.indexOf('/');
        if (sepIdx < 0) {
            if (getSafeName().equals(id)) {
                return this;
            }
        } else {
            String firstSubId = id.substring(0, sepIdx);
            String lastSubId = id.substring(sepIdx + 1);
            for (TestResult result : this.getChildren()) {
                if (result.getSafeName().equals(firstSubId)) {
                    return result.findCorrespondingResult(lastSubId);
                }
            }
        }
        return null;
    }

}
