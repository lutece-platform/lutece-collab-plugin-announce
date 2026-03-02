document.addEventListener('DOMContentLoaded', function() {
	var sortSelect = document.getElementById('idSort');
	if (sortSelect) {
		sortSelect.addEventListener('change', function() {
			var loc = '' + location;
			var n = loc.indexOf('jsp');
			var urlbase = loc.substring(0, n);
			var pageIndex = document.getElementById('idPageIndex').value;
			location = urlbase + 'jsp/site/Portal.jsp?page=announce&action=search&sortBy=' + this.value + '&page_index=' + pageIndex;
		});
	}

	var sectorSelect = document.getElementById('idSector');
	if (sectorSelect) {
		sectorSelect.addEventListener('change', function() {
			var idSec = this.value;
			var catSelect = document.getElementById('idCategory');
			var defaultLabel = catSelect.dataset.defaultLabel || '';
			catSelect.innerHTML = '<option value="0">' + defaultLabel + '</option>';

			var dataEl = document.getElementById('announce-sectors-data');
			if (!dataEl) return;

			var sectors = JSON.parse(dataEl.textContent);
			for (var i = 0; i < sectors.length; i++) {
				if (idSec == 0 || idSec == sectors[i].id) {
					var cats = sectors[i].categories;
					for (var j = 0; j < cats.length; j++) {
						var option = document.createElement('option');
						option.value = cats[j].id;
						option.textContent = cats[j].label;
						catSelect.appendChild(option);
					}
				}
			}
		});
	}
});
