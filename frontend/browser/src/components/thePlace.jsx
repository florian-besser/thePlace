import * as React from 'react';
import {connect} from 'react-redux';
import { SketchPicker } from 'react-color';
import {setColor, selectPixel, setPickerColor} from '../actions';


const PIXEL_SIZE = 10;

function ThePlaceComponent({board, onSelectPixel, setPickerColor, onSetColor}) {
    const width = board.xmaximum;
    const height = board.ymaximum;
    const selectedPixel = board.selectedPixel;
    return (
        <div>
            <div className='place' style={{width: width * PIXEL_SIZE, height: height * PIXEL_SIZE}}>
                {board.pixels.map(t => {
                    const isSelected = selectedPixel.x === t.x && selectedPixel.y === t.y;
                    return (
                        <Tile
                            color={t.color}
                            key={`${t.x}-${t.y}`}
                            selected={isSelected}
                            onSelect={() => onSelectPixel(t.x, t.y, '#' + t.color)}
                        />
                    )
                })}
            </div>
            <ColorSelector
                x={selectedPixel.x}
                y={selectedPixel.y}
                color={board.pickerColor}
                setPickerColor={setPickerColor}
                onSetColor={onSetColor}
            />
        </div>
    );
}

function Tile({color, selected, onSelect}) {
    const classes = selected ? 'tile selected' : 'tile';
    return (
        <div
            className={classes}
            style={{backgroundColor: `#${color}`}}
            onClick={onSelect}
        />
    );
}

function ColorSelector({x, y, color, setPickerColor, onSetColor}) {
    const hasSelectedPixel = !isNaN(x) && !isNaN(y);
    if (!hasSelectedPixel) {
        return (
            <div className='colorActions'>
                <h1>Select a Pixel</h1>
            </div>
        );
    }

    return (
        <div className='colorActions'>
            <p>Setting color for pixel {`${x}/${y}`} to {color}</p>
            <SketchPicker onChangeComplete={setPickerColor} color={color}/>
            <button onClick={() => onSetColor(x, y, color)}>Save</button>
        </div>
    );
}

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board
    }),
    (dispatch, ownProps) => ({
        onSetColor: (x, y, color) => dispatch(setColor(x, y, color)),
        onSelectPixel: (x, y, color) => dispatch(selectPixel(x, y, color)),
        setPickerColor: (color) => dispatch(setPickerColor(color.hex))
    })
)(ThePlaceComponent);
