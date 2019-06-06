from app.api import bp
from flask import url_for, jsonify, request
from app import db
from app.models import Challenge, Room
from app.api.errors import bad_request

@bp.route('/challenges', methods=['GET'])
def get_challenges():
    data = {
        "get_challenges": [challenge.to_dict() for challenge in db.session.query(Challenge).all()],
        "number_of_challenges": db.session.query(Challenge).count()
    }
    return jsonify(data)

@bp.route('/challenge/<string:name>', methods=['GET'])
def get_challenge_by_name(name):
    search_name = name
    return jsonify(db.session.query(Challenge).filter_by(name=search_name).first_or_404().to_dict())

@bp.route('/challenge', methods=['POST'])
def create_challenge():
    data = request.get_json() or {}
    if 'name' not in data:
        return bad_request('must include challengename')
    if db.session.query(Challenge).filter_by(name=data['name']).first():
        return bad_request('challenge aready existing')
    else:
        # add challenge
        challenge = Challenge()
        challenge.name = data['name']
        # rooms have to exist
        room_list = data['rooms'].split(',')
        for room in room_list:
            room = room.strip().upper()
            room_from_db = db.session.query(Room).filter_by(name = room).first()
            challenge.rooms.append(room_from_db)
        db.session.add(challenge)
        db.session.commit()
    response = jsonify(challenge.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.create_challenge', id=challenge.id)
    return response
