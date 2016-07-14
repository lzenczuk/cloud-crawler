

// ------------------- vars

var tickerAPI_url = '/public?command=returnTicker';
if (margin)tickerAPI_url += '&margin=1';
var allTickerData;

// these are declared in plx_exchange_br -- var marketBTCTable, sellOrdersTable, buyOrdersTable, tradeHistoryTable, myOrdersTable, marketXMRTable, marketUSDTTable;
// These are passed in from the PHP -- var currencyPairArray, currencyNamesArray

var marketTablesJsLoaded = false;

// ------------------- utility funcitons 

function exactRound(num, decimals) {
	if (decimals<0)decimals=0;
    var sign = num >= 0 ? 1 : -1;
    return (Math.round((num * Math.pow(10, decimals)) + (sign * 0.001)) / Math.pow(10, decimals)).toFixed(decimals);
}


// --------------- clicking, loading tables
function getCurrentPairDetails() {
	var row = allTickerData[currencyPair];
	var ch =  exactRound(row.percentChange * 100,2);
	var chPosNeg = '';
	if (ch < 0) { chPosNeg = 'neg';}
	if (ch >= 0) {
		ch = String('+') + ch;
	}
	var d = {
		name: getName(secondaryCurrency),
		pair: secondaryCurrency + '/' + primaryCurrency,
		last: row.last,
		change:  ch + '%',
		chPosNeg: chPosNeg,
		high: row.highestBid,
		low: row.lowestAsk,
		p0: primaryCurrency,
		p1: secondaryCurrency, 
		baseVol: row.baseVolume, 
		quoteVol: row.quoteVolume,
		high24hr: row.high24hr,
		low24hr: row.low24hr
	};
	return d;
}

function setCurrentMarketRowActive() {
	$('.box.markets').find('tr.active').removeClass('active');
	$('#baseTab'+primaryCurrency).click();

	// mark the new row active
	var pair = currencyPair.toLowerCase();
	var row = $('.box.markets').find('[data-url="' + pair + '"]');
	row.addClass('active');
}

function clickMarketTableRow(r) {
		var pair = r.parent().attr('data-url').toUpperCase();
		var loading = $('#chartLoading').is(':visible');
		if (loading) { return; }
		orderbookDisplayLimit = defaultOrderbookDisplayLimit;
		orderBookInitialLoad = 2;
		allChartData = {'300': {},'900': {},'1800': {},'7200': {},'14400': {},'86400': {}};
		
		// update the global var
		currencyPair = pair;

		// get the data obj, format as needed by updateMarketDisplay()
		var pairArr = currencyPair.split('_');
		primaryCurrency = pairArr[0];
		secondaryCurrency = pairArr[1];

		setCurrentMarketRowActive(); 
		window.location.hash = '#' + primaryCurrency.toLowerCase() + '_' + secondaryCurrency.toLowerCase();
		//evaluateHash() will take it from here
}

function activateMarketTableClicks() {
	// click table row to update big chart
	$('.markets tbody td').not('.star').click(function(e){
		e.preventDefault();
		// Detect if the user wants to open a new tab/window.
		if(e.shiftKey || e.ctrlKey){
			window.open((margin ? "/marginTrading#" : "/exchange#") + $(this).parent().attr('data-url'));
		} else {
			clickMarketTableRow($(this));
		}
	});

	$('.markets tbody td.star').click(function(){
		toggleStar($(this));
	});

	// Show star only
	$('#marketStar').change(function(){
		filterNonStarred();
		setFilterMessage();
		resetMatketTableHeights();
		saveExchangeSettings();
		showStarOnly = $(this).is(":checked");
	});
}


// ---------- Search / Star Filter

function filterNonStarred(){
	var fString
	if($('#marketStar').is(":checked")) {
		fString = '~';
	} else {
		fString = '';
	}
	$('#marketBTC').dataTable().fnFilter( fString, 0 );
	$('#marketETH').dataTable().fnFilter( fString, 0 );
	$('#marketXMR').dataTable().fnFilter( fString, 0 );
	$('#marketUSDT').dataTable().fnFilter( fString, 0 );
	setFilterMessage();
}

function clearMarketSearch(){
	$('#marketSearch').val("");
	$('#marketSearch').removeClass('x onX');
	$('#marketBTC').DataTable().search('').draw();
	$('#marketETH').DataTable().search('').draw();
	$('#marketXMR').DataTable().search('').draw();
	$('#marketUSDT').DataTable().search('').draw();
	if (showStarOnly === true) {
		$('#marketStar').prop("checked", true);
		filterNonStarred();
	}
	setFilterMessage();
}

function setFilterMessage(){
	if ($('#marketStar').is(":checked") || $('#marketSearch').val()){
		$('#marketTables').addClass('filtered');
	} else {
		$('#marketTables').removeClass('filtered');
	}
}

function resetMatketTableHeights(){
	var localEX = localStorage["exchangeSettings"];
	var settings = JSON.parse(localEX);

	if (typeof(settings.marketRows) != 'undefined') {
		showMarketLines(settings.marketRows);
	} else {
		$('#rowButtons .active').click();
	}
}



// ------------------- formatting

function getName(coin) {
	return currencyNamesArray[coin];
}

function formatTickerData(pair, tickerData) {
	// console.log('format ' + pair + ' : ',tickerData);
    var pairArray = pair.split("_"),
    	base = pairArray[0],
    	quote = pairArray[1],
        name = getName(quote),
        decimals;
	
	
    tickerData.url =  pair.toLowerCase();
    tickerData.pair = pair;
    tickerData.primary = base;
    tickerData.secondary = quote;
    tickerData.symbol = quote;
    tickerData.balance = 0.0;
    tickerData.value = 0.0;

    if (allBalances instanceof Object){
	    tickerData.balance = parseFloat(allBalances['balances'][quote]) + parseFloat(allBalances['onOrders'][quote]);
	    tickerData.value = tickerData.balance * parseFloat(tickerData.highestBid);
	}   
    
    tickerData.balance = tickerData.balance.toFixed(4);
	tickerData.value = tickerData.value.toFixed(4);
	
    allTickerData[pair]['balance'] = tickerData.balance;
    allTickerData[pair]['value'] = tickerData.value;
	
    tickerData.price = tickerData.last;

    tickerData.volume = exactRound(tickerData.baseVolume,3);
    tickerData.change = exactRound(tickerData.percentChange * 100,2);
    tickerData.changeDirection = "positive";
    tickerData.displayChange = tickerData.change;
    tickerData.name = "";
    tickerData.class = "";

    if(name){
        tickerData.name = name;
    }

    tickerData.frozen = '';
    if (tickerData.isFrozen === '1') { 
    	tickerData.frozen = 'frozen';
    }
    
    if(tickerData.change < 0) {
        tickerData.changeDirection = "neg";
    }
    else {
        tickerData.displayChange = '+' + tickerData.displayChange;
    }

    return tickerData;
}

function getRow(pair) {
	var d = formatTickerData(pair, allTickerData[pair]);
	var active = '';
	var starOff = '';
	var starContents = '~';
	var pair = (primaryCurrency + '_' + secondaryCurrency).toLowerCase();
	if (d.url == pair) { active = ' active'; }

	// if this is marked as non-starred in settings
	var starIndex = starSettings.indexOf(d.url.toUpperCase());
	if (starIndex > -1) {
		starOff = ' starOff';
		starContents = '';
	}

	var row = '<tr  data-url="' + d.url + '" id="marketRow' + d.url + '" class="marketRow ' + d.frozen + active + starOff + '">';
	row += '<td class="star"><i class="fa fa-star"></i><span class="starContent">' + starContents + '</span></td>';
	row += '<td class="coin">' + d.secondary + '</td>';
	if (!d.frozen){
		row += '<td class="price">' + d.price + '</td>';
		row += '<td class="volume">' + d.volume + '</td>';
		row += '<td class="change ' + d.changeDirection + '">' + d.displayChange + '</td>';
	} else {
		row += '<td class="price">&nbsp;FROZEN</td>'; // nbsp is used to make FROZEN appear last in sorting order
		row += '<td class="volume"></td>';
		row += '<td class="change"></td>';
	};

	row += '<td class="colName"><div class="ellipsis" title="' + d.name + '">' + d.name + '</div></td>';
	row += '<td class="colBalance">' + (loggedIn ? d.balance : '-') + '</td>';
	row += '<td class="colEstVal">' + (loggedIn ? d.value : '-') + '</td>';
	row += '</tr>';

	return row;
}


function writeMarketTable(coin) {
	var arr = currencyPairArray.filter(function(d, i) { 
		return (d.substr(0, coin.length) === coin); 
	});
	
	var rows = '';
	for (var i = 0; i < arr.length; i++) {
		rows += getRow(arr[i]);
	}

	$('#market' + coin + ' tbody').html(rows);
}

var marketTablesLoaded = 0;
function initMarketTables() {
	var marketTableOptions = {
    	paging: false,
    	autoWidth: true,
    	info: false,
    	//use datatables built in local storage function to save sort order and other table settings
    	stateSave: true, 
    	scrollY: 95,
    	scrollCollapse: true,
    	order: [[3, 'desc']],
    	language: { "emptyTable": "No markets to display", "zeroRecords": "No markets matching your filters" },
    	// Set the sort on some columns to be descending on first click
    	// If we add a new col to the table it needs to be defined here
    	aoColumns: [ 
			{ "asSorting": [ "desc", "asc" ] },
			null,
			{ "asSorting": [ "desc", "asc" ], "searchable": false },
			{ "asSorting": [ "desc", "asc" ], "searchable": false },
			{ "asSorting": [ "desc", "asc" ], "searchable": false },
			null,
			{ "asSorting": [ "desc", "asc" ], "searchable": false, "visible": false },
			{ "asSorting": [ "desc", "asc" ], "searchable": false, "visible": false }
		],
		fnInitComplete: function(oSettings, json) {
				marketTablesLoaded ++;
			}
    };

	marketBTCTable = $('#marketBTC').dataTable(marketTableOptions);
	marketETHTable = $('#marketETH').dataTable(marketTableOptions);
	marketXMRTable = $('#marketXMR').dataTable(marketTableOptions);
	marketUSDTTable = $('#marketUSDT').dataTable(marketTableOptions);

    //Prevent Datatables from saving the search filter settings to local storage
	marketBTCTable.dataTable()
		.on( 'stateSaveParams.dt', function (e, settings, data) {
	        data.search.search = "";
	    } );
	marketETHTable.dataTable()
		.on( 'stateSaveParams.dt', function (e, settings, data) {
	        data.search.search = "";
	    } );
	marketXMRTable.dataTable()
		.on( 'stateSaveParams.dt', function (e, settings, data) {
	        data.search.search = "";
	    } );
	marketUSDTTable.dataTable()
		.on( 'stateSaveParams.dt', function (e, settings, data) {
	        data.search.search = "";
	    } );


	// set up search to do all 3 markets
	$('#marketSearch').keyup(function(e){
		var s = $(this).val();
		if (s){
			$('#marketStar').prop('checked', false);
			filterNonStarred();
		}
		$('#marketBTC').DataTable().search(s).draw();
		$('#marketETH').DataTable().search(s).draw();
		$('#marketXMR').DataTable().search(s).draw();
		$('#marketUSDT').DataTable().search(s).draw();
		setFilterMessage();
		resetMatketTableHeights();
	});

	// Magnifying glass icon click
	$('.search .icon').click(function(){
		$('#marketSearch').focus();
	});

	$(document).bind('keyup', function(e) {
		// ESC to clear Filter
		if(e.keyCode == 27) {
			clearMarketSearch();
			resetMatketTableHeights();
		}
	});

	$('#marketFilterMessage .resetFilters').click(function() {
		clearMarketSearch();
		$('#marketStar').prop('checked', false).change();
		$('#marketTables').removeClass('filtered')
		resetMatketTableHeights();
	})

	$(document).bind('keydown', function(e) {
		// CTRL+F to Filter
		if(e.ctrlKey && (e.which == 70)) { 
			e.preventDefault();
			$('#marketSearch').focus();
			$('#marketSearch').select();
			$('#marketsContainer .search').addClass('highlight');
			setTimeout(function(){
			    $('#marketsContainer .search').removeClass('highlight');
			},750);
		return false;
		}
	});

	$('#marketSearch').bind('keydown', function(e) {
		// press ENTER to select first result
		if(e.keyCode == 13) {
			if($('#marketTables .marketContainer.active tbody tr:first-child').attr('data-url')) {
				clickMarketTableRow($('#marketTables .marketContainer.active tbody tr:first-child .coin'));
				//clearMarketSearch();
			}
		}
	});

	// X icon to clear Market Filter
	function tog(v){return v?'addClass':'removeClass';}
	$(document).on('input', '.clearable', function(){
		$(this)[tog(this.value)]('x');
	}).on('mousemove', '.x', function( e ){
		$(this)[tog(this.offsetWidth-14 < e.clientX-this.getBoundingClientRect().left)]('onX'); 
	}).on('click', '.onX', function(){
		clearMarketSearch();
		resetMatketTableHeights();
	});
  
	var scrollOpts = {
		verticalDragMinHeight: 20,
		contentWidth: 370
		};
	$('#marketBTC_wrapper .dataTables_scrollBody').jScrollPane(scrollOpts);
	$('#marketETH_wrapper .dataTables_scrollBody').jScrollPane(scrollOpts);
	$('#marketXMR_wrapper .dataTables_scrollBody').jScrollPane(scrollOpts);
	$('#marketUSDT_wrapper .dataTables_scrollBody').jScrollPane(scrollOpts);

  // Reset header widths for Safari bug
	$('#marketBTC').DataTable().draw();
	$('#marketETH').DataTable().draw();
	$('#marketXMR').DataTable().draw();
	$('#marketUSDT').DataTable().draw();

	// ToolPanel Button Clicks
	$('.markets .toolPanel button').click(function(){
		var theBU = $(this); // button
		var settingsRow = theBU.parent().parent().parent(); // the settingsRow
		settingsRow.find('.active').removeClass('active');

		if (settingsRow.hasClass('rowSettings')) {
			// Show rows
			theBU.addClass('active');
			var num = theBU.html(); // # rows
			showMarketLines(num);

			saveExchangeSettings();
		} else if (settingsRow.hasClass('colSettings')) {
			// Switch last col view
			theBU.addClass('active');
			var col = theBU.attr('data-url');

			showMarketColumn('BTC', col, true);
			showMarketColumn('ETH', col, true);
			showMarketColumn('XMR', col, true);
			showMarketColumn('USDT', col, true);

			saveExchangeSettings();
		} else {
			// Toggle the stars
			var type = theBU.attr('data-url');

			quickSelectStars('BTC', type);
			quickSelectStars('ETH', type);
			quickSelectStars('XMR', type);
			quickSelectStars('USDT', type);

		}
		hideAllToolPanels();
	});


	// Tabbed Sections
	$('#marketTables .tabs li').on('click', function(e) {
		e.preventDefault();
		if(!$(this).hasClass('active')){
			$('#marketTables .tabs').find('li').removeClass('active');
			$(this).addClass('active');
			
			$('#marketTables').find('.marketContainer').removeClass('active');
			target = $(this).attr('data-url');
			$('#'+target).addClass('active');

			var pane = $('#' + target + ' .jspScrollable');
			var api = pane.data('jsp');
			if (api) { api.scrollToY(0); }

			$('#rowButtons .active').click();
		}
	});

	$('.box.markets thead').show();
	updateMarketDisplay();
}


function showMarketLines(rows) {
	var rowHeight = 21;
	var targetHeight = 0; 
	var section = $('#marketTables section.active');
	var dt = section.find('.dataTables_scrollBody');
	var jsp = dt.find('.jspContainer');
	var tbl = jsp.find('.dataTable');
	var tableHeight = tbl.height();

	if (rows === 'ALL') {
		// show all
		targetHeight = tableHeight;
	} else {
		if((rows * rowHeight) >= tableHeight ){
			// the table is shorter than the default height, collapse
			targetHeight = tableHeight;
		} else {
			// the table is larger than the default height, fixed
			targetHeight = (rows * rowHeight);	
		}
	}
	
	// reset datatables scrollY val
	var dtObj =  tbl.dataTable();
	dtObj.fnSettings().oScroll.sY = targetHeight;
	dtObj.fnDraw(false); // false = do not resort/filter before redraw

	dt.stop().css({height: targetHeight});
	jsp.stop().css({height: targetHeight});
	dt.data('jsp').reinitialise(); //reinit scrollbars
}


// Right column toggle
function showMarketColumn(id, col, setSort) {
	$('#' + id + 'container').removeClass('collapsed');
	// Name = 5, Balance = 6, Value = 7
	var colNum = 5;
	var sortDir = 'asc';
	if (col === "Balance") {
		colNum = 6;
		sortDir = 'desc';
	} else if (col === "Value") {
		colNum = 7;
		sortDir = 'desc';
	}
	var t = $('#market' + id).DataTable();
	// console.log(t);
	t.column(5).visible(false);
	t.column(6).visible(false);
	t.column(7).visible(false);
	t.column(colNum).visible(true);
	if(setSort){
		t.order( [ colNum, sortDir ] ).draw();
	}
}


function quickSelectStars(id, type){
	$('#' + id + 'container').removeClass('collapsed');
	var t = $('#market' + id);

	// show all before selecting
	t.dataTable().fnFilter( '', 0 );
	clearMarketSearch();

	if(type === 'starAll'){
		$('tr.marketRow', t).each(function(){
        	var theTR = $(this);
        	var dataPair = theTR.attr('data-url').toUpperCase();

        	if(theTR.hasClass('starOff')){
				theTR.removeClass('starOff');
				var theTD = theTR.find('.star');
				t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent">~</span>');
				saveNonStarredMarket(dataPair);
        	}
		});
	} else if(type === 'starNone'){
		$('tr.marketRow', t).each(function(){
        	var theTR = $(this);
        	var dataPair = theTR.attr('data-url').toUpperCase();

        	if(!theTR.hasClass('starOff')){
				theTR.addClass('starOff');
				var theTD = theTR.find('.star');
				t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent"></span>');
				saveNonStarredMarket(dataPair, true);
			}
		});
	} else { // My Balances
		$('tr.marketRow', t).each(function(){
			var theTR = $(this);
        	var dataPair = theTR.attr('data-url').toUpperCase();
			var data = t.DataTable().row(theTR).data();
			var bal = data[6];
			if (bal > 0){ //switch on
	        	if(theTR.hasClass('starOff')){
					theTR.removeClass('starOff');
					var theTD = theTR.find('.star');
					t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent">~</span>');
					saveNonStarredMarket(dataPair);
	        	}
			} else { //switch off
				if(!theTR.hasClass('starOff')){
					theTR.addClass('starOff');
					var theTD = theTR.find('.star');
					t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent"></span>');
					saveNonStarredMarket(dataPair, true);
				}
			}
		});
	}
	filterNonStarred();
	resetMatketTableHeights();
}


// selecting individual stars
function toggleStar(s) {

	var theTR = s.parent();
	var t = theTR.parent().parent();
	var dataPair = theTR.attr('data-url').toUpperCase();

	if (theTR.hasClass('starOff')) {
		// star it
		theTR.removeClass('starOff');
		var theTD = theTR.find('.star');
		t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent">~</span>').draw();
		saveNonStarredMarket(dataPair);
	} else {
		// unstar it
		theTR.addClass('starOff');
		var theTD = theTR.find('.star');
		t.DataTable().cell(theTD).data('<i class="fa fa-star"></i><span class="starContent"></span>').draw();
		saveNonStarredMarket(dataPair, true);
		
		// fix a bug where if you're viewing * only and you ustar the ladt market the table height jumps
		resetMatketTableHeights();
	}

}



// ------------------- load ticker
function getTickerInfo(){
	$.getJSON(tickerAPI_url, function(d) {
		allTickerData = d;
		writeMarketTable('BTC');
		writeMarketTable('ETH');
		writeMarketTable('XMR');
		writeMarketTable('USDT');
		activateMarketTableClicks();
		initMarketTables();
	});
}

// ------------------- ready
var marketTablesJsLoaded;
$(document).ready(function(){
	marketTablesJsLoaded = true;
});