// Empty constructor
function PrinterPC901H2Plugin() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
PrinterPC901H2Plugin.prototype.load = function(successCallback, errorCallback) {
  var options = {};
  //options.message = message;
  cordova.exec(successCallback, errorCallback, 'PrinterPC901H2Plugin', 'load', [options]);
}

PrinterPC901H2Plugin.prototype.print = function(message, successCallback, errorCallback) {
  var options = {};
  options.message = message;
  cordova.exec(successCallback, errorCallback, 'PrinterPC901H2Plugin', 'print', [options]);
}

// Installation constructor that binds PrinterPC901H2Plugin to window
PrinterPC901H2Plugin.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.printerPC901H2Plugin = new PrinterPC901H2Plugin();
  return window.plugins.printerPC901H2Plugin;
};
cordova.addConstructor(PrinterPC901H2Plugin.install);