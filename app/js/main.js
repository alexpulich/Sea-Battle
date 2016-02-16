//Fullscreen scrolling midule
var scroll = (function() {
    var _fullpageElem = $('#fullpage'),

        _addScroll = function() {
            _fullpageElem.fullpage({
                anchors:['main','about'],
                menu: '#nav'
            });
        };

    return {
        init: _addScroll
    }
})();

var dragAndDrop = (function() {

    //Setup module
    function _setup() {
        _setDragAndDrop();
        $('.ship').on('click', _rotateShip);
        $('.game-field-inner').on('drop', _dropShip);
        $('.game-field-inner').on('dropout', _dropOutShip);
    }

    //Setting jQuery-UI droppable and draggable to
    //field and ships
    function _setDragAndDrop() {
        $('.game-field-inner').droppable({
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

    //Marking a cell with a ship's part as busy
    function _dropShip(event, ui) {
        $(this).removeClass('empty');
        $(this).addClass('busy');
        _getCoords($(this));
    }

    //Marking a cell without a ship's part as empty
    function _dropOutShip(event, ui) {
        $(this).removeClass('busy');
        $(this).addClass('empty');
    }

    //Getting a set of ship's coords 
    function _getCoords(elem) {
        var _x = $(elem).closest('.game-field-cell').index();
        var _y = $(elem).closest('.game-field-row').index();
        console.log("{" + _x + ";" + _y + "}");
    }

    //Changing orientation of a ship on clicking
    function _rotateShip() {
        var shipW = $(this).width(),
            shipH = $(this).height();

        $(this).width(shipH);
        $(this).height(shipW)  
        _getCoords($(this));
    }


    return {
        init: _setup,
        getCoords : _getCoords
    }
})();

//Loading overlay and progress module
var loader = (function() {
    var _load = null;

    function _setUpListeners() {
        $('#search').on('click', _addLoading);
    }

    function _addLoading() {
        _load = $('body').maskLoader({
            'background': 'black',
            'opacity': '0.8',
            'imgLoader': false
        });
        setTimeout(_removeLoading, 2000);
    }

    function _removeLoading() {
        _load.destroy();
    }

    return {
        init: _setUpListeners
    }
})();


var gamebot = (function() {
    var _BOTMODE = "pvbserver",
        _ip = "46.32.76.190",
        _port = "8000",
        _msg = "",
        _socket = null,
        _lastShot = null;

    function _setupListeners() {
        $('#bot').on('click', function() {
            _setupSocket(_BOTMODE);
        });
        $(window).on('unload', function() {
            if (_socket)
                _socket.close();
        })
        $('#random').on('click', _random);
        $('#confirm').on('click', _confirm);
    }

    function _random() {
        var msg = '"PlaceShipsRandom"';
        _send(msg);
    }

    function _placeShips(data) {
        var rows = $($('.game-field-table')[0]).find($('tr')),
            field = data;

            for (var i = 0; i < 10; i++ ) {
                for (var j = 0; j < 10; j++) {
                    var cell = rows.eq(i).children().eq(j).children('.game-field-inner');
                    if (field[i][j] == 'Void') {
                        cell.removeClass('busy');
                        cell.addClass('empty');
                    } else if (field[i][j] == 'Ship') {
                        cell.removeClass('empty');
                        cell.addClass('busy');
                    }
                }
            }
    }

    function _confirm() {
        var msg = '"StartBattle"';
        _send(msg);
    }   

    function _setupShotListener() {
        $('.game-field-cell').on('click', function() {
            var xVal = $(this).index();
            var yVal = $(this).closest('.game-field-row').index();
            _lastShot = {x: xVal, y: yVal};
            _send("Shot");
            // setTimeout(function() {_send(JSON.stringify(_lastShot));}, 3000);
        });
    }

    function _shot(cell) {
        _send(JSON.stringify(_lastShot));
    }

    function _battleResultHandler(result) {
        if (result === "Win")
            alert("You won!");
        else if (result === "Lose") 
            alert("You lost");
        else
            alert("WTF? We got neither WIN nor LOSE!");
    }

    function _noticeHandler(notice) {
        switch (notice) {
            case "YourTurn":
                _setupShotListener();
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
            console.log("first field");
            field = $('#player');
        }
        else if (data.fieldStatus === "Second") {
            console.log("second field");
            field = $('#enemy');
        }
        else {
            alert("what field did you mean?");
        }
        console.log("hit");
        console.log(data.hit);
        console.log("miss");
        console.log(data.misses);
        if (data.misses) {
            for (var i = 0; i < data.misses.length; i++) {
                var x = data.misses[i].x;
                var y = data.misses[i].y;
                _setFieldStatus(x, y, "lightsteelblue");
            }
        }
        if (data.hit) {
            var x = data.hit.x;
            var y = data.hit.y;
            _setFieldStatus(x, y, "darkorange")
        }

        function _setFieldStatus(x, y, color) {
            var row = field.find(".game-field-row").eq(y);
            row.find('.game-field-cell').eq(x).css({"background": color});
        }
    }

    function _setupSocket(mode) {
        _socket = new WebSocket("ws://" + _ip + ":" + _port + "/" + mode);

        _socket.onopen = function() {
            console.log("Connected successfuly");
        }
        _socket.onerror = function(error) {
            console.log("ERROR: " + error.data);
        } 
        _socket.onmessage = function(event) {
            msg = JSON.parse(event.data);
            console.log("Got a message with type: " + msg.type);
            switch (msg.type) {
                case "Cell[][]":
                    console.log("I'm a cell handler");
                    _placeShips(msg.data);
                    break;
                case "Notice":
                    console.log("I'm a notice handler");
                    console.log(msg.data);
                    _noticeHandler(msg.data);
                    break;
                case "BattleResult":
                    console.log("I'm a BattleResult handler");
                    console.log(msg.data);
                    _battleResultHandler(msg.data);
                    break;
                case "FieldChanges":
                    console.log("I'm a FieldChanges handler");
                    _fieldChangesHandler(msg.data);
                    break;
                default:
                    console.log("Not in switch: " + msg.data);
            }
        }
        _socket.onclose = function(event) {
            if (event.wasClean)
                console.log("Connection closed clearly");
            else 
                console.log("СONNECTION: was broken");
            console.log("Code: " + event.code + " reason: " + event.reason);
        }
    }

    function _send(msg) {
        if (!_socket) {
            console.log("Socket isn't set (" + _socket + ")");
            return;
        } 
        console.log("Sending: " + msg);
        _socket.send(msg);
    }

    return {
        init: _setupListeners
    }
})();


//Main part
$(document).ready(function() {
    if ($.find("#fullpage").length > 0) {
        scroll.init();
    }
    if ($.find('#search').length > 0) {
        loader.init();  
    }

    if($.find('.ship').length > 0) {
        dragAndDrop.init();
    }

    gamebot.init();
});