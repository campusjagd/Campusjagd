from app.api import bp

@bp.route('/rooms', methods=['GET'])
def get_rooms():
    data = {
        "rooms": [room.to_dict() for room in Room.query.all()],
        "number_of_rooms": Room.query.count()
    }
    return jsonify(data)

@bp.route('/room/<string:name>', methods=['GET'])
def get_room_by_name(name):
    search_name = name.upper()
    return jsonify(Room.query.get_or_404(name=search_name).to_dict())

@bp.route('/room', methods=['POST'])
def add_room():
    data = request.get_json() or {}
    if 'name' not in data:
        return bad_request('must include roomname')
    if Room.query.filter_by(name=data['name'].upper()).first():
        return bad_request('room aready existing')
    else:
        # add user with points
        room = Room()
        room.name = data['name']
        room.gpsposition = data['gpsposition']
        room.points = data['points']
        db.session.add(room)
        db.session.commit()
    response = jsonify(room.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.add_room', id=user.id)
    return response
