from app.api import bp
from flask import url_for, jsonify, request
from app import db
from app.models import Room
from app.api.errors import bad_request

@bp.route('/rooms', methods=['GET'])
def get_rooms():
    data = {
        "rooms": [room.to_dict() for room in db.session.query(Room).all()],
        "number_of_rooms": db.session.query(Room).count()
    }
    return jsonify(data)

@bp.route('/room/<string:name>', methods=['GET'])
def get_room_by_name(name):
    search_name = name.upper()
    return jsonify(db.session.query(Room).filter_by(name=search_name).first_or_404().to_dict())

@bp.route('/room', methods=['POST'])
def add_room():
    data = request.get_json() or {}
    if 'name' not in data or 'gpsposition' not in data:
        return bad_request('must include roomname and gpsposition')
    if db.session.query(Room).filter_by(name=data['name'].upper()).first():
        return bad_request('room aready existing')
    else:
        # add room with points
        room = Room()
        room.name = data['name']
        room.gpsposition = data['gpsposition']
        room.points = data['points']
        db.session.add(room)
        db.session.commit()
    response = jsonify(room.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.add_room', id=room.id)
    return response
