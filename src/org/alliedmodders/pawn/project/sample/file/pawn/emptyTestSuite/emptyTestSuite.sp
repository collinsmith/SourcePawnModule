#define TESTSUITE

#include <sourcemod>
#include <testing>

public Plugin pluginInfo = {
    name = "${name}",
    author = "${user}",
    description = "Tests for ",
    version = "0.0.1",
    url = "http://www.sourcemod.net/"
};

public void OnPluginStart() {
    RegServerCmd("tests", Command_TestAll);
}

void SetTestingContext() {
    char filename[32];
    GetPluginFilename(null, filename, sizeof filename - 1);
    SetTestContext(filename);
}

public Action Command_TestAll(int args) {
    Command_Test_(0);
}

public Action Command_Test_(int args) {
    SetTestingContext();
}