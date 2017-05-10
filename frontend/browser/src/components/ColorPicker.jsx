import * as React from "react";
import ReactTimeout from "react-timeout";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {ChromePicker} from "react-color";
import {setColor, setPickerColor, updateTimeout} from "../actions";


const ColorSelectorComponent = ReactTimeout(({colorSelector, setPickerColor, setColor, updateTimeout}) => {
    const {selectedPixel, selectedColor, timeoutExpiry, setColorError} = colorSelector;
    const hasSelectedPixel = !isNaN(selectedPixel.x) && !isNaN(selectedPixel.y);
    if (!hasSelectedPixel) {
        return (
            <div className='colorActions'>
                <h1>Select a Pixel</h1>
            </div>
        );
    }

    const diff = timeoutExpiry ? timeoutExpiry.diff() : 0;
    const disabled = diff > 0;
    if (disabled) {
        setTimeout(updateTimeout, 1000);
    }

    return (
        <div className='colorActions'>
            <p>Setting color for pixel {`${selectedPixel.x}/${selectedPixel.y}`} to {selectedColor}</p>
            {setColorError && <p>Failed to update Pixel.</p>}
            {disabled && <p>Waiting for timeout...</p>}
            <TimeoutClock millisRemaining={diff}/>
            {!disabled && (
                <div>
                    <ChromePicker disableAlpha onChangeComplete={setPickerColor} color={selectedColor}/>
                    <button onClick={() => setColor(selectedPixel.x, selectedPixel.y, selectedColor)}>Save</button>
                </div>
            )}
        </div>
    );
});

function TimeoutClock ({millisRemaining}) {
    if (millisRemaining <= 0) {
        return null;
    }

    return (
        <span>Wait for {Math.ceil(millisRemaining / 1000)} seconds</span>
    );
};

export const ColorSelector = connect(
    ({colorSelector}) => ({colorSelector}),
    dispatch => bindActionCreators({setColor, setPickerColor, updateTimeout}, dispatch)
)(ColorSelectorComponent);
