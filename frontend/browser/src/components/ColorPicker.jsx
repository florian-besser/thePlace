import * as React from "react";
import ReactTimeout from "react-timeout";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {ChromePicker} from "react-color";
import {setColor, setPickerColor, updateTimeout} from "../actions";


function ColorSelectorComponent({colorSelector, setPickerColor, setColor, updateTimeout}) {
    const {selectedPixel, selectedColor, timeoutExpiry} = colorSelector;
    const hasSelectedPixel = !isNaN(selectedPixel.x) && !isNaN(selectedPixel.y);
    if (!hasSelectedPixel) {
        return (
            <div className='colorActions'>
                <h1>Select a Pixel</h1>
            </div>
        );
    }

    const disabled = timeoutExpiry && timeoutExpiry.isAfter();

    return (
        <div className='colorActions'>
            <p>Setting color for pixel {`${selectedPixel.x}/${selectedPixel.y}`} to {selectedColor}</p>
            {disabled && <p>Waiting for timeout...</p>}
            <TimeoutClock timeout={timeoutExpiry} updateTimeout={updateTimeout}/>
            {!disabled && (
                <div>
                    <ChromePicker disableAlpha onChangeComplete={setPickerColor} color={selectedColor}/>
                    <button onClick={() => setColor(selectedPixel.x, selectedPixel.y, selectedColor)}>Save</button>
                </div>
            )}
        </div>
    );
}

const TimeoutClock = ReactTimeout(({timeout, updateTimeout, setTimeout}) => {
    const diff = timeout ? timeout.diff() : 0;
    if (diff <= 0) {
        return null;
    }

    if (diff > 0) {
        setTimeout(updateTimeout, 1000);
    }

    return (
        <span>Wait for {Math.ceil(diff / 1000)} seconds</span>
    );
});

export const ColorSelector = connect(
    (state, ownProps) => ({
        colorSelector: state.colorSelector
    }),
    dispatch => bindActionCreators({setColor, setPickerColor, updateTimeout}, dispatch)
)(ColorSelectorComponent);
