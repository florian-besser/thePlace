import * as React from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {selectPixel} from "../actions";
import {ColorSelector} from "./ColorPicker";


const PIXEL_SIZE = 10;

function ThePlaceComponent({board, colorSelector, selectPixel}) {
    const width = board.colors[0] ? board.colors[0].length : 0;
    const height = board.colors.length;
    return (
        <div>
            <div className='place' style={{width: width * PIXEL_SIZE, height: height * PIXEL_SIZE}}>
                {board.colors.map((line, y) => (
                    <TileRow
                        key={y}
                        y={y}
                        line={line}
                        selectPixel={selectPixel}
                        selectedPixel={colorSelector.selectedPixel}
                    />
                ))}
            </div>
            <ColorSelector />
        </div>
    );
}

function TileRow({y, line, selectPixel, selectedPixel}) {
    return (
        <div className='tileRow'>
            {
                line.map((color, x) => {
                    const isSelected = selectedPixel.x === x && selectedPixel.y === y;
                    return (
                        <Tile
                            color={color}
                            key={x}
                            selected={isSelected}
                            onSelect={() => selectPixel(x, y, color)}
                        />
                    );
                })
            }
        </div>
    );
}

function Tile({color, selected, onSelect}) {
    const classes = selected ? 'tile selected' : 'tile';
    return (
        <div
            className={classes}
            style={{backgroundColor: `${color}`}}
            onClick={onSelect}
        />
    );
}

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board,
        colorSelector: state.colorSelector
    }),
    dispatch => bindActionCreators({selectPixel}, dispatch)
)(ThePlaceComponent);
