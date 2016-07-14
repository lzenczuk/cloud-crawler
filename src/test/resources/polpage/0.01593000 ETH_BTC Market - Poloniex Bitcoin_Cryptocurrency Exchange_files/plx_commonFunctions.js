function hideAllToolPanels() {
    $('.toolPanel, .helpPanel').fadeOut(200);
    $('.tools.active, .help.active').removeClass('active');
}

function restoreTrollboxSettingsFromStorage(s){
	if (typeof(s.trollboxCollapsed) != 'undefined'){
		if (s.trollboxCollapsed === true) {
			$('#TrollboxContainer').addClass('collapsed');
		}
	}
}

function loadTrollboxSettings(){
	var settings;
	var localEX = localStorage["trollboxSettings"];
	if(localEX === undefined){
		var exchangeSettings = localStorage["exchangeSettings"];
		if (exchangeSettings === undefined){
	        saveTrollboxSettings();
	    } else {
		    // Migrate trollbox settings from exchange settings
		    exchangeSettings = JSON.parse(exchangeSettings);
		    if (exchangeSettings['trollboxRows'] !== undefined && exchangeSettings['trollboxCollapsed'] !== undefined){
		    	settings = {'trollboxRows': exchangeSettings['trollboxRows'], 'trollboxCollapsed': exchangeSettings['trollboxCollapsed']};
				localStorage.setItem('trollboxSettings', JSON.stringify(settings));
		    } else {
			    saveTrollboxSettings();
		    }
	    }
	    localEX = localStorage["trollboxSettings"];
    }
    
	settings = JSON.parse(localEX);
	restoreTrollboxSettingsFromStorage(settings);
}

function saveTrollboxSettings(){
	var localEX = localStorage["trollboxSettings"];
	if (typeof(localEX) != 'undefined') {
		var oldSettings = JSON.parse(localEX);
	} else {
		var oldSettings = {};
	}
	
	var settings = {
	    trollboxCollapsed: $('#TrollboxContainer').hasClass('collapsed')
    };
    
    var trollboxRowSettings = $('#buttonsTrollbox .button.active').html();
    
    if (trollboxRowSettings != undefined) {
		settings.trollboxRows = trollboxRowSettings;
	}  else {
		if (oldSettings.trollboxRows != undefined) {
			settings.trollboxRows = oldSettings.trollboxRowSettings;
		} else {
			settings.trollboxRows = trollboxRowSettings;
		}
	}

	localStorage.setItem('trollboxSettings', JSON.stringify(settings));
}