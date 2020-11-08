/**
 * RemoteConnection prototype class
 */
var RemoteConnection = (function() {
  var instance = {};

  /**
   * Public constructor
   */
  function RemoteConnection(dashboard) {
    instance.dashboard = dashboard;

    this.socket = atmosphere;
    this.isConnected = false;
  }

  /**
   * Start a new remote connection
   * @param transportType with the transport type
   */
  RemoteConnection.prototype.start = function(transportType) {
    if (this.isConnected) {
      instance.dashboard.setErrorMessage("The remote connection is already opened. Close before start a new one");
    }
    else {
      // Create a new request for a remote connection
      var request = {
        url: document.location.toString() + 'prices/' + transportType,
        contentType: 'application/json',
        transport: transportType,
        fallbackTransport: 'long-polling'
      };

      // Create the listener 'open' on the request
      request.open = function(response) {
        instance.dashboard.setErrorMessage("Atmosphere connected using " + response.transport);
      }


      // Create the listener 'onMessage' on the request
      request.onMessage = function(response) {
        var instrument = JSON.parse(response.responseBody);
        instance.dashboard.addPrice(instrument);
      };

      // Create the listener 'onClose' on the request
      request.onClose = function(response) {
        instance.dashboard.setErrorMessage("Atmosphere disconnected from " + response.transport);
      };

      // Create the listener 'onError' on the request
      request.onError = function(response) {
        instance.dashboard.setErrorMessage("Sorry, but there is a problem with your socket or the server is down " + response.transport);
      };


      // Subscribe using the request
      this.socket.subscribe(request);

      // Set isConnected to true
      this.isConnected = true;

      // Set the error message as empty
      instance.dashboard.setErrorMessage("");
    }
  }

  /**
   * Stop the current remote connection
   */
  RemoteConnection.prototype.stop = function() {
    if (!this.isConnected) {
      instance.dashboard.setErrorMessage("The remote connection is already closed. Open before close a new one.");
    }
    else {
      // unsubscribed
      this.socket.unsubscribe();


      // Set isConnected to false
      this.isConnected = false;

      // Set the error message as empty
      instance.dashboard.setErrorMessage("");
    }
  }

  return RemoteConnection;

}());