var scroll = (function() {
    var _fullpageElem = $('#fullpage'),

        _addScroll = function() {
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
    var _ships = [];

    function _initShips() {
        for (var i = 1; i < 5; i++) {
            var ship = {
                "length": 1,
                "startPos": {"x": 10, "y": 10},
                "orientation": "horizontal",
                "block": $('#ship1' + i)
            };
            _ships.push(ship);
        }
        for (var i = 1; i < 4; i++) {
            var ship = {
                "length": 2,
                "startPos": {"x": 10, "y": 10},
                "orientation": "horizontal",
                "block": $('#ship2' + i)
            };
            _ships.push(ship);
        }
        for (var i = 1; i < 3; i++) {
            var ship = {
                "length": 3,
                "startPos": {"x": 10, "y": 10},
                "orientation": "horizontal",
                "block": $('#ship3' + i)
            };
            _ships.push(ship);
        }
        var ship = {
            "length": 4,
            "startPos": {"x": 10, "y": 10},
            "orientation": "horizontal",
            "block": $('#ship41');
        };
        _ships.push(ship);
    }

    return {
        init: _initShips
    }

})();

var dragAndDrop = (function() {

    function _setup() {
        _setDragAndDrop();
        $('.ship').on('click', _rotateShip);
        $('#player .game-field-inner').on('drop', _dropShip);
        $('#player .game-field-inner').on('dropout', _dropOutShip);
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
        var _x = $(elem).closest('.game-field-cell').index();
        var _y = $(elem).closest('.game-field-row').index();
        console.log("{" + _x + ";" + _y + "}");
    }

    function _rotateShip() {
        var shipW = $(this).width(),
            shipH = $(this).height();

        $(this).width(shipH);
        $(this).height(shipW)  
        _getCoords($(this));
    }


    return {
        init: _setup,
    }
})();


var gamebot = (function() {
    var _BOTMODE = "pvbserver",
        _PLAYERMODE = 'pvpserver',
        _IP = "46.32.76.190",
        _PORT = "8000",
        _VERTICAL = "vertical",
        _HORIZONTAL = "horizontal",
        _msg = "",
        _socket = null,
        _lastShot = null,
        _loader = null,
        _ships = [];

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
        _lastShot = { x: xVal, y: yVal };
        _send("Shot");
    }

    function _random() {
        var msg = '"PlaceShipsRandom"';
        _send(msg);
    }

    function _placeShips(data) {
        var rows = $($('.game-field-table')[0]).find($('tr'));
        console.log(data);

        _clearField();

        var ship_1 = 1,
            ship_2 = 1,
            ship_3 = 1,
            ship_4 = 1;

        for (var i = 0; i < data.length; i++) {
            var ship = {
                "length": 0,
                "startPos": {"x": 10, "y": 10},
                "orientation": null,
                "block": null
            };
            
            ship.length = data[i].length;

            for (var j = 0; j <ship.length; j++) {
                var cell = rows.eq(data[i][j].x).children().eq(data[i][j].y).children('.game-field-inner');
                cell.addClass('busy');
                cell.removeClass('empty');
                cell.closest('.game-field-cell').addClass('ship-in');
                ship.startPos.x = Math.min(ship.startPos.x, data[i][j].x);
                ship.startPos.y = Math.min(ship.startPos.y, data[i][j].y);
            }

            switch (ship.length) {
                case 1:
                    ship.block = $('#ship1'+ship_1++);
                    break;
                case 2:
                    ship.block = $('#ship2'+ship_2++);
                    break;
                case 3:
                    ship.block = $('#ship3'+ship_3++);
                    break;
                case 4:
                    ship.block = $('#ship4'+ship_4++);
                    break;
                default: 
                    console.log("Such length of ship (" + ship.length + ") is not supported!");
            }

            if (ship.length > 1) {
                ship.orientation = (data[i][0].x === data[i][1].x) ? _HORIZONTAL : _VERTICAL; 
                _rotateShip(ship);
            } else {
                ship.orientation = _HORIZONTAL;
            }
            
            _setShipPosOnField(ship);
            _addShip(ship);
        }
    }

    function _rotateShip(ship) {
        var shipW = ship.block.width(),
            shipH = ship.block.height();

        if (ship.orientation == _VERTICAL && (shipH < shipW)) {
            ship.block.width(shipH);
            ship.block.height(shipW);
        } else if (ship.orientation == _HORIZONTAL && (shipH > shipW)) {
            ship.block.width(shipH);
            ship.block.height(shipW);
        }
    }

    function _setShipPosOnField(ship) {
        var tablePos = $($('.game-field-table')[0]).offset();
        ship.block.offset({
            top: tablePos.top + 1 + ship.startPos.x * 30,
            left: tablePos.left + 1 + ship.startPos.y * 30,
        });
    }

    function _addShip(ship) {
        if (!(ship.length && ship.startPos && ship.orientation && ship.block)) {
            console.log("Ship doesn't contain smth of: length, startPos, orientation, block");
            return;
        } 
        _ships.push(ship);
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
            case "Error":
                _loader.destroy();
                break;
            case "ExpectedCoordinates":
                _shot();
                break;
            default:
                break;
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

    return {
        init: _setupListeners,
        ships: _ships
    }
})();




//Main part
$(document).ready(function() {
    if ($.find("#fullpage").length > 0)
        scroll.init();

    if($.find('.ship').length > 0) {
        dragAndDrop.init();
    }
    if ($.find('.game-section').length > 0)
        gamebot.init();
});
