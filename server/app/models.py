from app import db
from sqlalchemy import Table, Column, Integer, ForeignKey, DateTime, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

room_challenge = db.Table('room_challenge', Base.metadata,
    db.Column('Room_id', db.Integer, db.ForeignKey('Room.id')),
    db.Column('Challenge_id', db.Integer, db.ForeignKey('Challenge.id'))
)
class Room(Base):
    __tablename__='Room'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    gpsposition = db.Column(db.String(200))
    points = db.Column(db.Integer)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    # challenges = db.relationship('Challenge', secondary=room_challenge, back_populates="rooms")
    def to_dict(self):
        data = {
            "id": self.id,
            "name": self.name,
            "gpsposition": self.gpsposition,
            "points": self.points,
            "timestamp": self.timestamp
            }
        return data

class Challenge(Base):
    __tablename__='Challenge'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    official = db.Column(db.Boolean, default=False)
    rooms = db.relationship('Room', secondary=room_challenge, backref="challenges")
    def to_dict(self):
        data = {
            "id": self.id,
            "name": self.name,
            "official": self.official,
            "rooms": [room.to_dict() for room in self.rooms],
        }
        return data

class User(Base):
    __tablename__='User'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    points = db.Column(db.Integer)
    def to_dict(self):
        data = {
            "id": self.id,
            "name": self.name,
            "points": self.points
        }
        return data
