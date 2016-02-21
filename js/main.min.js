var scroll = (function() {
  var _fullpageElem = $('#fullpage');

  function _addScroll() {
    _fullpageElem.fullpage({
      anchors: ['main', 'about'],
      menu: '#nav'
    });
  };

  return {
    init: _addScroll
  }
})();


var shipsModule = (function() {
  var _ships = [],
    _VERTICAL = "vertical",
    _HORIZONTAL = "horizontal";

  function _initShips() {
    for (var i = 1; i < 5; i++) {
      var ship = {
        "shipLength": 1,
        "startPos": {
          "x": 10,
          "y": 10
        },
        "orientation": _HORIZONTAL,
        "block": $('#ship1' + i)
      };
      _ships.push(ship);
    }
    for (var i = 1; i < 4; i++) {
      var ship = {
        "shipLength": 2,
        "startPos": {
          "x": 10,
          "y": 10
        },
        "orientation": _HORIZONTAL,
        "block": $('#ship2' + i)
      };
      _ships.push(ship);
    }
    for (var i = 1; i < 3; i++) {
      var ship = {
        "shipLength": 3,
        "startPos": {
          "x": 10,
          "y": 10
        },
        "orientation": _HORIZONTAL,
        "block": $('#ship3' + i)
      };
      _ships.push(ship);
    }
    var ship = {
      "shipLength": 4,
      "startPos": {
        "x": 10,
        "y": 10
      },
      "orientation": _HORIZONTAL,
      "block": $('#ship41')
    };
    _ships.push(ship);
  }

  function _getShipCoords(ship) {
    var coordsArray = [],
      x = ship.startPos.x,
      y = ship.startPos.y;

    if (ship.orientation === _HORIZONTAL) {
      for (var i = 0; i < ship.shipLength; i++) {
        coordsArray.push({
          "x": x,
          "y": y++
        });
      }
    } else {
      for (var i = 0; i < ship.shipLength; i++) {
        coordsArray.push({
          "x": x++,
          "y": y
        });
      }
    }
    return coordsArray;
  }

  function _getShipById(id) {
    for (var i = 0; i < 10; i++) {
      if (_ships[i].block.attr('id') == id) {
        return _ships[i];
      }
    }
  }

  function _setShipPosOnField(ship) {
    var tablePos = $($('.game-field-table')[0]).offset();
    ship.block.offset({
      top: tablePos.top + 1 + ship.startPos.x * 30,
      left: tablePos.left + 1 + ship.startPos.y * 30,
    });
  }

  function _rotateShip(ship, mode) {
    console.log("ROTATE MOTHERFUCKER!");
    if (mode === "user") {
      gameModule.changeCoords(ship, "RemoveShip");
      ship.orientation = (ship.orientation == _HORIZONTAL) ? _VERTICAL : _HORIZONTAL;
    }

    var shipW = ship.block.width(),
      shipH = ship.block.height();

    if (ship.orientation == _VERTICAL && (shipH < shipW)) {
      ship.block.width(shipH);
      ship.block.height(shipW);
    } else if (ship.orientation == _HORIZONTAL && (shipH > shipW)) {
      ship.block.width(shipH);
      ship.block.height(shipW);
    }
    if (mode === "user") {
      setTimeout(function() {
        gameModule.changeCoords(ship, "AddShip");
      }, 1000);
    }
  }

  return {
    init: _initShips,
    getShipById: _getShipById,
    rotateShip: _rotateShip,
    setShipPosOnField: _setShipPosOnField,
    getShipCoords: _getShipCoords
  }

})();


var dragAndDrop = (function() {
  var _tempCoords = {
      "x": 10,
      "y": 10
    },
    ship = null,
    _tempStartPos = null;

  function _setup() {
    _setDragAndDrop();
    $('.ship').on('click', _rotateShip);
    $('#player .game-field-inner').on('drop', _dropShip);
    $('#player .game-field-inner').on('dropout', _dropOutShip);
    $('.ship').on('dragstop', _stopDragging);
    $('.ship').on('dragstart', _startDragging);
  }

  function _setDragAndDrop() {
    $('#player .game-field-inner').droppable({
      scope: 'drop',
      tolerance: 'touch',
    });

    $('.ship').draggable({
      scope: 'drop',
      containment: "body",
      revert: "invalid",
      cursor: 'move',
      opacity: 0.8,
      snap: '.game-field-cell',
      snapMode: "inner",
    });
  }

  function _revertShip(prevX, prevY) {
    ship.startPos.x = prevX;
    ship.startPos.y = prevY;
    shipsModule.setShipPosOnField(ship);
  }

  function _stopDragging(event, ui) {
    ship = shipsModule.getShipById($(this).attr('id'));

    var coords = shipsModule.getShipCoords(ship);

    gameModule.changeCoords(ship, "AddShip");
  }

  function _startDragging(event, ui) {
    _tempCoords = {
      "x": 10,
      "y": 10
    };

    ship = shipsModule.getShipById($(this).attr('id'));
    _tempStartPos = ship.startPos;
    console.log("_TEMPSTARTPOS");
    console.log(_tempStartPos);

    var coords = shipsModule.getShipCoords(ship);

    gameModule.changeCoords(ship, "RemoveShip");
  }

  function _dropShip(event, ui) {
    $(this).removeClass('empty');
    $(this).addClass('busy');
    $(this).closest('.game-field-cell').addClass('ship-in');
    _getCoords($(this));
  }

  function _dropOutShip(event, ui) {
    $(this).removeClass('busy');
    $(this).closest('.game-field-cell').removeClass('ship-in');
    $(this).addClass('empty');
  }

  function _getCoords(elem) {
    var _y = $(elem).closest('.game-field-cell').index();
    var _x = $(elem).closest('.game-field-row').index();

    _tempCoords.x = Math.min(_tempCoords.x, _x);
    _tempCoords.y = Math.min(_tempCoords.y, _y);
    ship.startPos.x = (_tempCoords.x);
    ship.startPos.y = (_tempCoords.y);
  }

  function _rotateShip() {
    ship = shipsModule.getShipById($(this).attr('id'));
    shipsModule.rotateShip(ship, "user");
  }

  return {
    init: _setup,
    revertShip: _revertShip
  }
})();


var gameModule = (function() {
  var _BOTMODE = "pvbserver",
    _PLAYERMODE = 'pvpserver',
    _IP = "46.32.76.190",
    _PORT = "8000",
    _VERTICAL = "vertical",
    _HORIZONTAL = "horizontal",
    _msg = "",
    _socket = null,
    _lastShot = null,
    _lastRemoveShipCoords = null,
    _lastAddShipCoords = null,
    _loader = null;


  function _setupListeners() {
    $('#bot').on('click', function() {
      _initGame(_BOTMODE);
    });
    $('#search').on('click', function() {
      _initGame(_PLAYERMODE);
    })
    $(window).on('unload', _closeSocket);
    $('#enemy .game-field-cell').on('click', _shotClickHandler)
    $('#random').on('click', _random);
    $('#confirm').on('click', _confirm);
  }

  function _initGame(mode) {
    _closeSocket();
    _clearField();
    _setupSocket(mode);
    $('#search').addClass('inactive');
    $('#bot').addClass('inactive');
    $('#random').removeClass('inactive');
    $('#confirm').removeClass('inactive');
  }

  function _clearField() {
    $('.ship-in').removeClass('ship-in');
    $('.game-field-cell').removeClass('hit miss');
    $('.game-field-inner').removeClass('busy');
    $('.game-field-inner').addClass('empty');
  }

  function _shotClickHandler() {
    var yVal = $(this).index(),
      xVal = $(this).closest('.game-field-row').index();
    _lastShot = {
      x: xVal,
      y: yVal
    };
    _send("Shot");
  }

  function _random() {
    var msg = '"PlaceShipsRandom"';
    _send(msg);
  }

  function _placeShips(data) {
    _clearField();
    console.log(data);
    var rows = $('#player').find($('tr'));

    var shipsCounter = {
      "1": 1,
      "2": 1,
      "3": 1,
      "4": 1
    }

    for (var i = 0; i < data.length; i++) {
      shipLength = data[i].length;

      id = 'ship' + shipLength + (shipsCounter[shipLength]++);
      ship = shipsModule.getShipById(id);
      ship.startPos.x = 10;
      ship.startPos.y = 10;

      for (var j = 0; j < shipLength; j++) {
        var cell = rows.eq(data[i][j].x).children().eq(data[i][j].y).children('.game-field-inner');
        cell.addClass('busy');
        cell.removeClass('empty');
        cell.closest('.game-field-cell').addClass('ship-in');
        ship.startPos.x = Math.min(ship.startPos.x, data[i][j].x);
        ship.startPos.y = Math.min(ship.startPos.y, data[i][j].y);
      }

      if (ship.shipLength > 1) {
        ship.orientation = (data[i][0].x === data[i][1].x) ? _HORIZONTAL : _VERTICAL;
        shipsModule.rotateShip(ship, "server");
      }

      shipsModule.setShipPosOnField(ship);
    }
  }

  function _confirm() {
    var msg = '"StartBattle"';
    _send(msg);
    _setLoader();
  }

  function _setLoader() {
    _loader = $('#enemy').maskLoader({
      'background': 'black',
      'opacity': '0.3',
      'z-index': 999,
      'imgLoader': "http://www.animatedimages.org/data/media/271/animated-ship-image-0060.gif"
    });
  }

  function _shot(cell) {
    _send(JSON.stringify(_lastShot));
    _setLoader();
  }

  function _battleResultHandler(result) {
    if (result === "Win")
      alert("You won!");
    else if (result === "Lose")
      alert("You lost");
  }

  function _noticeHandler(notice) {
    switch (notice) {
      case "YourTurn":
      case "OpponentFound":
        _loader.destroy();
        break;
      case "ExpectedCoordinates":
        _shot();
        break;
      case "ExpectedRemoveShip":
        _sendShipCoords("remove");
        break;
      case "ExpectedAddShip":
        _sendShipCoords("add");
        break
      case "ShipAdded":
        // dragAndDrop.setRevert(false);
        break
      case "OpponentLeft":
        alert("Игрок вышел");
        _closeSocket();
        break;
      default:
        console.log("Not handled notice: " + notice);
        break;
    }
  }

  function _errorHandler(error) {
    switch (error) {
      case "ShotRepeated":
        _loader.destroy();
        alert("You've already shooted here");
        break;
      case "BattleNotStart":
        alert("You should confirm placement of your ships!");
        break;
      case "NotYourTurn":
        alert("It's not your turn! Just wait!");
        break;
      case "IncorrectShip":
        dragAndDrop.revertShip(_lastRemoveShipCoords[0].x,_lastRemoveShipCoords[0].y);
        break;
      default:
        console.log("Not handled error: " + error);
    }
  }

  function _fieldChangesHandler(data) {
    var field = null;
    if (data.fieldStatus === "First") {
      field = $('#player');
    } else if (data.fieldStatus === "Second") {
      field = $('#enemy');
    } else {
      console.log(data.fieldStatus + " - this is incorrect type of field");
    }
    if (data.misses) {
      for (var i = 0; i < data.misses.length; i++) {
        var x = data.misses[i].x;
        var y = data.misses[i].y;
        _setFieldStatus(x, y, "miss");
      }
    }
    if (data.hit) {
      var x = data.hit.x;
      var y = data.hit.y;
      _setFieldStatus(x, y, "hit")

      if (data.fieldStatus = "First") {
        var tablePos = $($('.game-field-table')[0]).offset();
        var fire = $('<div class="fire"/>'); 
        $('body').append(fire);
        console.log("THE FIRE SHOULD HAD BEEN ALREADY CREATED");
        fire.offset({
          top: tablePos.top + 1 + x * 30,
          left: tablePos.left + 1 + y * 30,
        });
      }
    }

    function _setFieldStatus(x, y, colorClass) {
      var row = field.find(".game-field-row").eq(x);
      row.find('.game-field-cell').eq(y).addClass(colorClass);
    }
  }

  function _setupSocket(mode) {
    _socket = new WebSocket("ws://" + _IP + ":" + _PORT + "/" + mode);

    _socket.onopen = function() {
      console.log("Connected successfuly");
      if (mode === _PLAYERMODE) {
        _loader = $('body').maskLoader({
          'background': 'white',
          'opacity': '0.7',
          'z-index': 999,
          'imgLoader': "http://www.animatedimages.org/data/media/271/animated-ship-image-0046.gif"
        });
      }
    }
    _socket.onerror = function(error) {
      console.log("ERROR: " + error.data);
    }
    _socket.onmessage = function(event) {
      msg = JSON.parse(event.data);
      console.log("Got a message with type: " + msg.type + "; data: " + msg.data);
      switch (msg.type) {
        case "ArrayList":
          _placeShips(msg.data);
          break;
        case "Notice":
          _noticeHandler(msg.data);
          break;
        case "Error":
          _errorHandler(msg.data);
          break;
        case "BattleResult":
          _battleResultHandler(msg.data);
          break;
        case "FieldChanges":
          _fieldChangesHandler(msg.data);
          break;
        default:
          console.log("Not in switch");
      }
    }
    _socket.onclose = function(event) {
      if (event.wasClean)
        console.log("Connection closed clearly");
      else
        console.log("Connection was broken");
      console.log("Code: " + event.code + " reason: " + event.reason);
    }
  }

  function _send(msg) {
    if (!_socket) {
      console.log("Socket was not set (" + _socket + ")");
      return;
    }
    console.log("Sending: " + msg);
    _socket.send(msg);
  }

  function _closeSocket() {
    if (_socket)
      _socket.close();
  }

  function _sendShipCoords(mode) {
    if (mode === "remove") {
      _send(JSON.stringify(_lastRemoveShipCoords));
    } else if (mode === "add") {
      _send(JSON.stringify(_lastAddShipCoords));
    }
  }

  function _changingCoordsHandler(ship, mode) {
    if (mode === "RemoveShip") {
      _lastRemoveShipCoords = shipsModule.getShipCoords(ship);
      for (var i = 0; i < _lastRemoveShipCoords.length; i++) {
        var x = _lastRemoveShipCoords[i].x,
          y = _lastRemoveShipCoords[i].y;
        var row = $('#player .game-field-row').eq(x),
          cell = row.find('.game-field-cell').eq(y);
        cell.removeClass('ship-in');
        cell.find('.game-field-inner').removeClass('busy');
        cell.find('.game-field-inner').addClass('empty');
      }
    } else if (mode === "AddShip") {
      _lastAddShipCoords = shipsModule.getShipCoords(ship);
      for (var i = 0; i < _lastAddShipCoords.length; i++) {
        var x = _lastAddShipCoords[i].x,
          y = _lastAddShipCoords[i].y;
        var row = $('#player .game-field-row').eq(x),
          cell = row.find('.game-field-cell').eq(y);
        cell.addClass('ship-in');
        cell.find('.game-field-inner').addClass('busy');
        cell.find('.game-field-inner').removeClass('empty');
      }
    }
    _send(mode);
  }

  return {
    init: _setupListeners,
    changeCoords: _changingCoordsHandler
  }
})();


$(document).ready(function() {
  if ($.find("#fullpage").length > 0)
    scroll.init();

  shipsModule.init();

  if ($.find('.ship').length > 0) {
    dragAndDrop.init();
  }
  if ($.find('.game-section').length > 0)
    gameModule.init();
});
