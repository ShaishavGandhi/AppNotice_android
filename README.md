## In-App Consent APK Overview

This repository is comprised of two workspaces: __InAppConsent__ and __Use_InAppConsentSDK__.

####InAppConsent
This workspace is where the In-App Consent APK is built. It includes these two projects:

InAppConsentSDK project: This is the SDK project. It can be built and used directly as a module, exported as an AAR and exported as a JAR.

Use_SDK_module project: This project uses the InAppConsentSDK project as a dependent module. It is used to rapidly develop and test the InAppConsentSDK without having to export it as a library.

####Use_InAppConsentSDK
This workspace is used to develop test apps that include InAppConsentSDK as an AAR or as a JAR. It includes these three projects:

InAppConsentSDK: This project is a duplicate of the InAppConsentSDK project from the __InAppConsent__ workspace with these changes:
1. The java source code has been removed.
2. The JAR that is exported from InAppConsent.InAppConsentSDK is included in the libs folder.
3. Text and graphics changes are made to customize it for the Use_SDK_jar (IceCube) test/sample app.
This project is included in the Use_SDK_jar project as a module and provides In-App Consent functionality to the resulting IceCube app.
   *	Note: This module folder (after the JAR processing listed below) (along with documentation) is the product that gets sent to clients who use Eclipse.

Use_SDK_aar: This project includes the InAppConsentSDK project as an AAR and provides In-App Consent functionality to the resulting WaterDrop app.

Use_SDK_jar: This project includes the InAppConsentSDK project as a JAR-based module and provides In-App Consent functionality to the resulting WaterDrop app.

###Build AAR and JAR
To build and use the AAR and JAR, follow these steps:

####Initial Processing

*	Clean the InAppConsent workspace. This causes the AAR library files to be rebuilt.

*	Build the JAR file manually via Gradle

    *	Expand the Gradle panel in AS

    *	Double-click InAppConsent.InAppConsentSDK.jarrelease


####AAR

*	Copy this folder: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\outputs

    *	To a new location for archiving the ProGuard mapping.

*	Copy this folder: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\libs

    *	To the copy of the "outputs" folder from the previous sub-step

*	Copy this file: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\outputs\aar\InAppConsentSDK-release.aar

    *	To this folder: …\InAppConsent_android\Use_InAppConsentSDK\Use_SDK_aar\libs\

*	Rename the copied AAR library:
 	From InAppConsentSDK-release.aar
    To InAppConsentSDK.aar

*	Unzip InAppConsentSDK.aar to a new folder

*	Edit applicable strings in …\InAppConsentSDK\res\values\values.xml

    *	For example:
    	ghostery_app_desc_1
        	From "Our company with help from…"
        	To "(YourCompanyName) with help from…"
    	ghostery_dialog_header_text
        	From "We Care About Your Privacy"
        	To "(YourCompanyName) Cares About Your Privacy"

    *	For AAR WaterDrop app it may look like this:
```JavaScript
        <!-- Common strings to customize: -->
        <string name="ghostery_manage_preferences_description">WaterDrop with help from our partners, collects data about your use of this app. We respect your privacy and if you would like to limit the data we collect please use the control panel below. To find out more about how we use data please visit our privacy policy.</string>
        <string name="ghostery_dialog_explicit_message">Our application uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to explicitly tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. To give us your consent, click on the \"Accept\" button.</string>
        <string name="ghostery_dialog_header_text">WaterDrop Cares About Your Privacy</string>
        <string name="ghostery_dialog_implicit_intro_message">Learn about how to set your tracking options.</string>
        <string name="ghostery_dialog_implicit_message">Our application uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. Further use of this app will be considered consent.</string>
        <string name="ghostery_dialog_button_preferences">Manage Preferences</string>
        <string name="ghostery_ric_max_default">3</string>
        <string name="ghostery_ric_session_max_default">1</string>
```

*	Update any graphics or logos in …\InAppConsentSDK\res\...

*	Zip the contents of the unzipped AAR back into a ZIP file

*	Copy that ZIP file back to lib folder it came from.

*	Delete the old AAR file and rename the new ZIP file to InAppConsentSDK.aar
   *	Note: This AAR file (along with documentation) is the product that gets sent to clients who use Android Studio.

*	Clean and rebuild the AAR project.


####JAR

*	Copy the JAR file to the SDK lib folder: …\YourProject\InAppConsentSDK\libs

*	Delete the SDK res folder: …\YourProject\InAppConsentSDK\src\main\res

*	Copy the inappnotice res folder to the SDK res folder

*	Edit applicable strings in …\YourProject\InAppConsentSDK\src\main\res\values\ghostery_strings.xml

*	For example:
    	ghostery_app_desc_1
            From "Our company with help from…"
            To "(YourCompanyName) with help from…"
    	ghostery_dialog_header_text
        	From "We Care About Your Privacy"
            To "(YourCompanyName) Cares About Your Privacy"

*	Update any graphics or logos in …\YourProject\InAppConsentSDK\src\main\res\...

*	Open the inappnotice.jar zip container (or unzip and rezip after edit) and remove all .class files in this folder: com\ghostery\privacy\InAppConsentSDK\. (Don't delete files from sub folders.):

```JavaScript
        R$anim.class
        R$attr.class
        R$bool.class
        R$color.class
        R$dimen.class
        R$drawable.class
        R$id.class
        R$integer.class
        R$layout.class
        R$menu.class
        R$raw.class
        R$string.class
        R$style.class
        R$styleable.class
        R.class
```

*	Clean and rebuild the JAR project.

