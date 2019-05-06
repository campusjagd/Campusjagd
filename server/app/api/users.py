from app.api import bp
from flask import url_for, jsonify
from app import db
from app.models import User
from app import models
from app.api.errors import bad_request

@bp.route('/users', methods=['GET'])
def get_users():
    data = {
        "users": [user.to_dict() for user in db.session.query(User).all()],
        "number_of_users": db.session.query(User).count()
    }
    return jsonify(data)

@bp.route('/users', methods=['PUT'])
def update_user():
    data = request.get_json() or {}
    if 'name' not in data:
        return bad_request('must include username')
    if db.session.query(User).filter_by(name=data['name']).first():
        # update user points
        user = db.session.query(User).filter_by(name=data['name']).first()
        user.points = data['points']
        db.session.add(user)
        db.session.commit()
    else:
        # add user with points
        user = User()
        user.name = data['name']
        user.points = data['points']
        db.session.add(user)
        db.session.commit()
    response = jsonify(user.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.update_user', id=user.id)
    return response

@bp.route('/users', methods=['POST'])
def add_users():
    data = request.get_json() or {}
    if 'name' not in data:
        return bad_request('must include username')
    if db.session.query(User).filter_by(name=data['name']).first():
        return bad_request('user aready existing')
    else:
        # add user with points
        user = User()
        user.name = data['name']
        user.points = data['points']
        db.session.add(user)
        db.session.commit()
    response = jsonify(user.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.add_user', id=user.id)
    return response
