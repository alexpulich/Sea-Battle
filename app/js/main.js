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

  /*
   * Генерация объектов-кораблей, имеющих следующие поля:
   * shipLength - длина корабля
   * startPos {x, y} - начальные координаты
   * block - соответствующий jQuery div объект корабля
  */
 
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

  //Возвращает массив координат корабля  [ {x,y}, ... ]
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

  //Возвращает объект ship из _ships на основе id блока корабля
  function _getShipById(id) {
    for (var i = 0; i < 10; i++) {
      if (_ships[i].block.attr('id') == id) {
        return _ships[i];
      }
    }
  }

  //устанавливает корабль в нужную позицию на поле на основе его стартовой.
  function _setShipPosOnField(ship) {
    var tablePos = $($('.game-field-table')[0]).offset();
    ship.block.offset({
      top: tablePos.top + 1 + ship.startPos.x * 30,
      left: tablePos.left + 1 + ship.startPos.y * 30,
    });
  }

  //изменяет поле orientation, затем размеры блока. При этом отправляет RemoveShip, AddShip
  function _rotateShip(ship, mode) {
    if (mode === "user") {
      gameModule.changeCoords(ship, "RemoveShip"); //отправляет все координаты корабля на сервер с пометкой RemoveShip
      ship.orientation = (ship.orientation == _HORIZONTAL) ? _VERTICAL : _HORIZONTAL; //меняет параметр ориентации корабля
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

  //установка слушателей событий и подключение draggable-droppable
  function _setup() {
    _setDragAndDrop();
    $('#player .game-field-inner').on('drop', _dropShip);
    $('.ship').on('dragstop', _stopDragging);
    $('.ship').on('dragstart', _startDragging);
  }

  //подключение draggable & droppable. Disable.
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

    _enableDragAndDrop(false);
  }

  //Вкл/выкл dragAndDrop
  function _enableDragAndDrop(flag) {
    if (flag) {
      $('#player .game-field-inner').droppable("enable");
      $('.ship').draggable("enable");
      $('.ship').on('click', _rotateShip);
    } else {
      $('#player .game-field-inner').droppable("disable");
      $('.ship').draggable("disable");
      $('.ship').off('click', _rotateShip);
      
    }
  }

  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!TODO!
  function _revertShip(prevX, prevY, prevOrientation) {
    ship.startPos.x = prevX;
    ship.startPos.y = prevY;
    ship.orientation = prevOrientation;
    shipsModule.rotateShip(ship);
    shipsModule.setShipPosOnField(ship);
  }

  /*
   * При прекращении перемещения:
   * * Получаем корабль
   * * Получаем его координаты
   * * Отправляем запрос на смену координат с пометой AddShip
  */
  function _stopDragging(event, ui) {
    ship = shipsModule.getShipById($(this).attr('id'));
    var coords = shipsModule.getShipCoords(ship);
    gameModule.changeCoords(ship, "AddShip");
  }

  /*
   * При начале перемещения:
   * * Получаем корабль
   * * Сохраняем текущую начальную позицию
   * * Получаем координаты корабля
   * * Отправляем запрос на смену координат с пометку RemoveShip
   * * 
   */
  function _startDragging(event, ui) {
    _tempCoords = {
      "x": 10,
      "y": 10
    };

    ship = shipsModule.getShipById($(this).attr('id'));
    _tempStartPos = ship.startPos;

    var coords = shipsModule.getShipCoords(ship);

    gameModule.changeCoords(ship, "RemoveShip");
  }

  //Сохраняем у корабля его начальную позицию
  function _dropShip(event, ui) {
    _setShipStartPos($(this));
  }

  //Сохраняем кораблю его начальные координаты
  function _setShipStartPos(elem) {
    var _y = $(elem).closest('.game-field-cell').index();
    var _x = $(elem).closest('.game-field-row').index();

    _tempCoords.x = Math.min(_tempCoords.x, _x);
    _tempCoords.y = Math.min(_tempCoords.y, _y);
    ship.startPos.x = _tempCoords.x;
    ship.startPos.y = _tempCoords.y;
  }

  //Получаем корабль и вызываем поворот блока
  function _rotateShip() {
    ship = shipsModule.getShipById($(this).attr('id'));
    shipsModule.rotateShip(ship, "user");
  }

  return {
    init: _setup,
    revertShip: _revertShip,
    enableDragAndDrop: _enableDragAndDrop
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
    _lastOrientation = null,
    _loader = null;

  //установка слушателей событий
  function _setupListeners() {
    $('#bot').on('click', function() {
      _initGame(_BOTMODE);
      dragAndDrop.enableDragAndDrop(true);
    });
    $('#search').on('click', function() {
      _initGame(_PLAYERMODE);
      dragAndDrop.enableDragAndDrop(true);
    })
    $(window).on('unload', _closeSocket);
    $('#enemy .game-field-cell').on('click', _shotClickHandler)
    $('#random').on('click', _placeShipsRandom);
    $('#confirm').on('click', _confirmShipsPlacement);
  }

  //подключаемся к серверу и очищаем поле
  function _initGame(mode) {
    _closeSocket();
    _clearField();
    _setupSocket(mode);
    $('#search').addClass('inactive');
    $('#bot').addClass('inactive');
    $('#random').removeClass('inactive');
    $('#confirm').removeClass('inactive');
  }

  //очищаем поле
  function _clearField() {
    $('.game-field-cell').removeClass('hit miss');
  }

  /*
   * Обработка выстрела
   * Сохраняем координаты выстрела для дальнейшней отправки
   */
  function _shotClickHandler() {
    var yVal = $(this).index(),
      xVal = $(this).closest('.game-field-row').index();
    _lastShot = {
      x: xVal,
      y: yVal
    };
    _send("Shot");
  }

  // Отправляем запрос на рандомную расстановку кораблей
  function _placeShipsRandom() {
    var msg = '"PlaceShipsRandom"';
    _send(msg);
  }

  /*
   * Установка кораблей
   * * Очищаем поле
   * * Устанавливаем корабли по данным с сервера
   */
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

  //Подтверждаем расстановку, отправляем запрос на начало игры
  function _confirmShipsPlacement() {
    var msg = '"StartBattle"';
    _send(msg);
    _setLoader();
  }

  //Включаем ожидающий загрузчик на поле противника
  function _setLoader() {
    _loader = $('#enemy').maskLoader({
      'background': 'black',
      'opacity': '0.3',
      'z-index': 999,
      'imgLoader': "http://www.animatedimages.org/data/media/271/animated-ship-image-0060.gif"
    });
  }

  //Отправляем координаты выстрела и включаем загрузчик
  function _sendShotCoords(cell) {
    _send(JSON.stringify(_lastShot));
    _setLoader();
  }

  //Обработчик результата битвы
  function _battleResultHandler(result) {
    if (result === "Win")
      var note = noty({
        text: "Поздравляем! Вы победили!",
        theme: "relax",
        type: "success",
        layout: 'topRight',
        timeout: "1000"
      });
    else if (result === "Lose")
      var note = noty({
        text: "К сожалению, вы проиграли!",
        theme: "relax",
        type: "error",
        layout: 'topRight',
        timeout: "1000"
      });
  }

  //Обработчик notice
  function _noticeHandler(notice) {
    switch (notice) {
      case "YourTurn":
      case "OpponentFound":
        _loader.destroy();
        break;
      case "ExpectedCoordinates":
        _sendShotCoords();
        break;
      case "ExpectedRemoveShip":
        _sendShipCoords("remove");
        break;
      case "ExpectedAddShip":
        _sendShipCoords("add");
        break;
      case "ShipAdded":
        break;
      case "OpponentLeft":
        var note = noty({
          text: "Ваш противник отключился!",
          theme: "relax",
          type: "warning",
          layout: 'topRight',
          timeout: "1000"
        });
        _closeSocket();
        break;
      default:
        console.log("Not handled notice: " + notice);
        break;
    }
  }

  //Обработчик error
  function _errorHandler(error) {
    switch (error) {
      case "ShotRepeated":
        _loader.destroy();
        var note = noty({
          text: "Вы уже стреляли по этой позиции!",
          theme: "relax",
          type: "warning",
          layout: 'topRight',
          timeout: "800"
        });
        break;
      case "BattleNotStart":
        var note = noty({
          text: "Сначала подтвердите расстановку ваших кораблей!",
          theme: "relax",
          type: "warning",
          layout: 'topRight',
          timeout: "1000"
        });
        break;
      case "NotYourTurn":
        var note = noty({
          text: "Пожалуйста, дождитесь своего хода!",
          theme: "relax",
          type: "warning",
          layout: 'topRight',
          timeout: "1000"
        });
        break;
      case "IncorrectShip":
        var note = noty({
          text: "Неккоректное расположение корабля!",
          theme: "relax",
          type: "warning",
          layout: 'topRight',
          timeout: "1000"
        });
        dragAndDrop.revertShip(_lastRemoveShipCoords[0].x,_lastRemoveShipCoords[0].y, _lastOrientation);
        // Костыль при перевороте корабля
        // _lastAddShipCoords = _lastRemoveShipCoords;
        // _send("AddShip")
        // Конец костыля
        break;
      default:
        console.log("Not handled error: " + error);
    }
  }

  //Обработчик изменения полей: помечаем промах/попадание, добавляем огонь
  function _fieldChangesHandler(data) {
    console.log("FIELDCHANGESHANDLER");
    console.log(data);
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
      console.log("THERE IS A HIT!");
      var x = data.hit.x;
      var y = data.hit.y;
      _setFieldStatus(x, y, "hit")

      if (data.fieldStatus == "First") {
        var tablePos = $($('.game-field-table')[0]).offset();
        var fire = $('<div class="fire"/>'); 
        $('body').append(fire);
        fire.offset({
          top: tablePos.top + 1 + x * 30,
          left: tablePos.left + 1 + y * 30,
        });
        console.log("HAVE CREATED FIRE BLOCK!");
      }
    }

    function _setFieldStatus(x, y, colorClass) {
      var row = field.find(".game-field-row").eq(x);
      row.find('.game-field-cell').eq(y).addClass(colorClass);
    }
  }

  //Подключение сокета и настройка его событий
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
          console.log(msg.data);
          _fieldChangesHandler(msg.data);
          break;
        default:
          console.log("Not in switch");
      }
    }
    _socket.onclose = function(event) {
      _loader.destroy();
      var messageType = null;
      if (event.wasClean) {
        messageType = "warning";
        console.log("Connection closed clearly");
      }
      else {
        messageType = "error"
        console.log("Connection was broken");
      }
      var note = noty({
        text: "Соединение прервано!",
        theme: "relax",
        type: messageType,
        layout: 'topRight',
        timeout: "1500"
      });
      console.log("Code: " + event.code + " reason: " + event.reason);
    }
  }

  //отправка запроса
  function _send(msg) {
    if (!_socket || (_socket.readyState == 3)) {
      var note = noty({
        text: "Соединение не установлено!",
        theme: "relax",
        type: "error",
        layout: 'topRight',
        timeout: "1000"
      });
      return;
    }
    console.log("Sending: " + msg);
    _socket.send(msg);
  }

  //закрытие сокета
  function _closeSocket() {
    if (_socket)
      _socket.close();
  }

  //отправка координат корабля
  function _sendShipCoords(mode) {
    if (mode === "remove") {
      _send(JSON.stringify(_lastRemoveShipCoords));
    } else if (mode === "add") {
      _send(JSON.stringify(_lastAddShipCoords));
    }
  }

  //Отправка изменённых координат
  function _changingCoordsHandler(ship, mode) {
    if (mode === "RemoveShip") {
      _lastRemoveShipCoords = shipsModule.getShipCoords(ship);
      _lastOrientation = ship.orientation;
      for (var i = 0; i < _lastRemoveShipCoords.length; i++) {
        var x = _lastRemoveShipCoords[i].x,
          y = _lastRemoveShipCoords[i].y;
        var row = $('#player .game-field-row').eq(x),
          cell = row.find('.game-field-cell').eq(y);
      }
    } else if (mode === "AddShip") {
      _lastAddShipCoords = shipsModule.getShipCoords(ship);
      for (var i = 0; i < _lastAddShipCoords.length; i++) {
        var x = _lastAddShipCoords[i].x,
          y = _lastAddShipCoords[i].y;
        var row = $('#player .game-field-row').eq(x),
          cell = row.find('.game-field-cell').eq(y);
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
