/**
 * Conditional questions support for radio buttons and checkboxes.
 * Shows/hides child questions based on selected field values.
 * Toggles 'required' on inputs inside conditional divs to prevent
 * hidden mandatory fields from blocking form submission.
 */

function hide( fieldId ) {
    var el = document.getElementById( 'div' + fieldId );
    if ( el ) {
        el.style.display = 'none';
        el.style.visibility = 'hidden';
        el.querySelectorAll( '[required]' ).forEach( function( input ) {
            input.removeAttribute( 'required' );
            input.setAttribute( 'data-required', 'true' );
        });
    }
}

function doDisplay( fieldId ) {
    var el = document.getElementById( 'div' + fieldId );
    if ( el ) {
        el.style.display = 'block';
        el.style.visibility = 'visible';
        el.querySelectorAll( '[data-required]' ).forEach( function( input ) {
            input.setAttribute( 'required', '' );
            input.removeAttribute( 'data-required' );
        });
    }
}

function doCheckboxEffect( checked, fieldId ) {
    if ( checked ) {
        doDisplay( fieldId );
    } else {
        hide( fieldId );
    }
}

document.addEventListener( 'DOMContentLoaded', function() {
    document.querySelectorAll( '.form-element-conditionnel' ).forEach( function( div ) {
        if ( div.style.display === 'none' || div.offsetParent === null ) {
            div.querySelectorAll( '[required]' ).forEach( function( input ) {
                input.removeAttribute( 'required' );
                input.setAttribute( 'data-required', 'true' );
            });
        }
    });
});
