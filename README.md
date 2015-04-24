# InAppConsent_android
In-App Consent SDK

This repository is comprised of two workspaces: InAppConsent and Use_InAppConsentSDK.

The InAppConsent workspace is where the In-App Consent SDK is built. It includes these two projects:

InAppConsentSDK project: This is the SDK project. It can be built and used directly as a module, exported as an AAR and exported as a JAR.

Use_SDK_module project: This project uses the InAppConsentSDK project as a dependent module. It is used to rapidly develop and test the InAppConsentSDK without having to export it as a library.

The Use_InAppConsentSDK workspace is used to develop test apps that include the Use_InAppConsentSDK as an AAR or as a JAR. It includes these three projects:

InAppConsentSDK: This project is a duplicate of the InAppConsentSDK project from the InAppConsent workspace with these changes:
1. The java source code has been removed.
2. The JAR that is exported from InAppConsent.InAppConsentSDK is included in the libs folder.
3. Text and graphics changes are made to customize it for the Use_SDK_jar (IceCube) test/sample app.
This project is included in the Use_SDK_jar project as a module and provides In-App Consent functionality to the resulting IceCube app.

Use_SDK_aar: This project includes the InAppConsentSDK project as an AAR and provides In-App Consent functionality to the resulting WaterDrop app.

Use_SDK_jar: This project includes the InAppConsentSDK project as a JAR-based module and provides In-App Consent functionality to the resulting WaterDrop app.

To build and use the AAR and JAR, follow these steps:

Initial Processing

1.	Clean the InAppConsent workspace. This causes the AAR library files to be rebuilt.

2.	Build the JAR file manually via Gradle

    a.	Expand the Gradle panel in AS

    b.	Double-click InAppNotice.InAppNoticeSDK.jarrelease


AAR

1.	Copy this folder: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\outputs

a.	To a new location for archiving the ProGuard mapping.

2.	Copy this folder: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\libs

    a.	To the copy of the "outputs" folder from the previous sub-step

3.	Copy this file: …\InAppConsent_android\InAppConsent\InAppConsentSDK\build\outputs\aar\InAppConsentSDK-release.aar

    a.	To this folder: …\InAppConsent_android\Use_InAppConsentSDK\Use_SDK_aar\libs\

4.	Rename the copied AAR library:
 .	From InAppConsentSDK-release.aar
    To InAppConsentSDK.aar

5.	Unzip InAppConsentSDK.aar to a new folder

6.	Edit applicable strings in …\InAppConsentSDK\res\values\values.xml

    a.	For example:
    i.	ghostery_app_desc_1
      1.	From "Our company with help from…"
      2.	To "(YourCompanyName) with help from…"
    ii.	ghostery_dialog_header_text
      1.	From "We Care About Your Privacy"
      2.	To "(YourCompanyName) Cares About Your Privacy"

    b.	For AAR WaterDrop app it may look like this:
        <!-- Common strings to customize: -->
        <string name="app_name">CompanyName</string>
        <string name="ghostery_app_desc_1">WaterDrop with help from our partners, collects data about your use of this app. We respect your privacy and if you would like to limit the data we collect please use the control panel below. To find out more about how we use data please visit our privacy policy.</string>
        <string name="ghostery_app_desc_2"/>
        <string name="ghostery_app_desc_3"/>
        <string name="ghostery_dialog_explicit_message">Our application uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to explicitly tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. To give us your consent, click on the \"Accept\" button.</string>
        <string name="ghostery_dialog_header_text">WaterDrop Cares About Your Privacy</string>
        <string name="ghostery_dialog_implicit_intro_message">Learn about how to set your tracking options.</string>
        <string name="ghostery_dialog_implicit_message">Our application uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. Further use of this app will be considered consent.</string>
        <string name="ghostery_dialog_button_preferences">Manage Preferences</string>
        <string name="ghostery_ric_max_default">3</string>
        <string name="ghostery_ric_session_max_default">1</string>

7.	Update any graphics or logos in …\InAppConsentSDK\res\...

8.	Zip the contents of the unzipped AAR back into a ZIP file

9.	Copy that ZIP file back to lib folder it came from.

10.	Delete the old AAR file and rename the new ZIP file to InAppConsentSDK.aar

11.	Clean and rebuild the AAR project.


JAR

1.	Copy the JAR file to the SDK lib folder: …\YourProject\InAppConsentSDK\libs

2.	Delete the SDK res folder: …\YourProject\InAppConsentSDK\src\main\res

3.	Copy the inappnotice res folder to the SDK res folder

4.	Edit applicable strings in …\YourProject\InAppConsentSDK\src\main\res\values\ghostery_strings.xml

5.	For example:
    a.	ghostery_app_desc_1
        From "Our company with help from…"
        To "(YourCompanyName) with help from…"
    b.	ghostery_dialog_header_text
     .	From "We Care About Your Privacy"
        To "(YourCompanyName) Cares About Your Privacy"

6.	Update any graphics or logos in …\YourProject\InAppConsentSDK\src\main\res\...

7.	Open the inappnotice.jar zip container (or unzip and rezip after edit) and remove all .class files in this folder: com\ghostery\privacy\InAppConsentSDK\. (Don't delete files from sub folders.):

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

8.	Clean and rebuild the JAR project.

