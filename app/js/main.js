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
        init: _setup
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
    var socket;

    function _setup() {
        $('#bot').on('click', _botgame);
        $('#random').on('click', _random);
        $('#confirm').on('click', _confirm);
    }

    function _random() {
        var msg = '"PlaceShipsRandom"';
        socket.send(msg);
        console.log("sending: " + msg);
        var field;
        socket.onmessage = function(event) {
            console.log("MSG: " + event.data);
            var rows = $($('.game-field-table')[0]).find($('tr'));
            field = JSON.parse(event.data);
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
    }

    function _confirm() {
        var msg = '"StartBattle"';
        socket.send(msg);
        console.log("sending: " + msg);
    }   

    function _botgame() {
        socket = new WebSocket("ws://46.32.76.190:8000/pvbserver");

        socket.onopen = function() {
            console.log("CONNECTION: Connected successfuly");
        }
        socket.onerror = function(error) {
            console.log("ERROR: " + error.data);
        } 
        socket.onmessage = function(event) {
            console.log("MESSAGE: " + event.data);
        }
        socket.onclose = function(event) {
            if (event.wasClean)
                console.log("CONNECTION: closed clearly");
            else 
                console.log("СONNECTION: was broken");
            console.log("Code: " + event.code + " reason: " + event.reason);
        }
    }

    return {
        init: _setup
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