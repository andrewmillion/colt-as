package codeOrchestra.colt.as.ui.settingsForm

import codeOrchestra.colt.as.model.AsProject
import codeOrchestra.colt.as.ui.settingsForm.compilerSettings.BuildSettingsForm
import codeOrchestra.colt.as.ui.settingsForm.compilerSettings.CompilerSettingsForm
import codeOrchestra.colt.as.ui.settingsForm.compilerSettings.SDKSettingsForm
import codeOrchestra.colt.as.ui.settingsForm.liveSettings.LauncherForm
import codeOrchestra.colt.as.ui.settingsForm.liveSettings.LiveSettingsForm
import codeOrchestra.colt.as.ui.settingsForm.liveSettings.SettingsForm
import codeOrchestra.colt.as.ui.settingsForm.liveSettings.TargetForm
import codeOrchestra.colt.as.ui.settingsForm.projectPaths.ProjectPathsForm
import codeOrchestra.colt.as.ui.settingsForm.projectPaths.TemplateForm
import codeOrchestra.colt.core.tracker.GAController
import codeOrchestra.colt.core.ui.components.scrollpane.SettingsScrollPane
import codeOrchestra.colt.core.ui.components.advancedSeparator.AdvancedSeparator
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.util.Duration
import codeOrchestra.colt.as.model.ModelStorage

/**
 * @author Dima Kruk
 */
class AsSettingsForm extends SettingsScrollPane{

    private Button saveAndRunButton
    EventHandler saveRunAction

    AdvancedSeparator separator

    private List<IFormValidated> validatedForms

    AsProject project = ModelStorage.instance.project

    AsSettingsForm() {

        validatedForms = new ArrayList<>()

        //paths
        ProjectPathsForm projectPaths = new ProjectPathsForm()
        projectPaths.first = true
        validatedForms.add(projectPaths)
        mainContainer.children.add(projectPaths)
        //paths

        separator = new AdvancedSeparator()
        mainContainer.children.add(separator)

        saveAndRunButton = separator.saveButton
        saveAndRunButton.onAction = saveRunAction

        VBox advancedVBox = new VBox()
        VBox.setMargin(advancedVBox, new Insets(0, 0, 72, 0))
        advancedVBox.padding = new Insets(0, 0, 18, 0)
        separator.content = advancedVBox
        mainContainer.children.add(advancedVBox)

        TemplateForm template = new TemplateForm()
        template.first = true
        validatedForms.add(template)
        advancedVBox.children.add(template)
        //liveSettings
        TargetForm target = new TargetForm()
        validatedForms.add(target)
        target.ownerForm = this
        advancedVBox.children.add(target)


        LauncherForm launcher = new LauncherForm()
        project.projectBuildSettings.runTargetModel.target().addListener({ ObservableValue<? extends String> observableValue, String t, String newValue ->
            launcher.disable = newValue != codeOrchestra.colt.as.run.Target.SWF.name()
        } as ChangeListener)
        validatedForms.add(launcher)
        advancedVBox.children.add(launcher)

        LiveSettingsForm liveSettings = new LiveSettingsForm()
        advancedVBox.children.add(liveSettings)

        SettingsForm lSettings = new SettingsForm()
        advancedVBox.children.add(lSettings)
        //liveSettings

        //compilerSettings
        SDKSettingsForm sdkSettings = new SDKSettingsForm()
        validatedForms.add(sdkSettings)
        advancedVBox.children.add(sdkSettings)

        BuildSettingsForm buildSettings = new BuildSettingsForm()
        validatedForms.add(buildSettings)
        advancedVBox.children.add(buildSettings)

        CompilerSettingsForm compilerSettings = new CompilerSettingsForm()
        advancedVBox.children.add(compilerSettings)
        //compilerSettings

        GAController.instance.registerPage(this, "/as/asSettings.html", "asSettings")
    }

    void setSaveRunAction(EventHandler saveRunAction) {
        this.saveRunAction = saveRunAction
        saveAndRunButton.onAction = {
            if(validateForms()) {
                this.saveRunAction.handle(it)
            }
        } as EventHandler
    }

    public boolean validateForms(IFormValidated skipForm = null) {
        Parent invalidNode = null
        validatedForms.each {
            Parent node = it.validated()
            if (it != skipForm && node && invalidNode == null) {
                invalidNode = node
            }
        }
        if (invalidNode != null) {
            scrollTo(invalidNode)
        }
        return invalidNode == null
    }

    private scrollTo(Parent node) {
        Bounds separatorBounds = content.sceneToLocal(separator.localToScene(separator.layoutBounds))
        Bounds nodeBounds = content.sceneToLocal(node.localToScene(node.layoutBounds))
        if (nodeBounds.minY < separatorBounds.minY) {
            scrollToNode(node)
        } else if (separator.close) {
            separator.close = false
            Timeline timeline = new Timeline(new KeyFrame(new Duration(50), {
                scrollToNode(node)
            } as EventHandler))
            timeline.play()
        } else {
            scrollToNode(node)
        }
    }
}
