<#assign licenseFirst = "/*">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

#include <sourcemod>

public Plugin pluginInfo = {
    name = "${name}",
    author = "${user}",
    description = "",
    version = "0.0.1",
    url = "http://www.sourcemod.net/"
};

public void OnPluginStart() {
    //...
}

public void OnPluginEnd() {
    //...
}