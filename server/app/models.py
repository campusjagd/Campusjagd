from app import db
from sqlalchemy import Table, Column, Integer, ForeignKey, DateTime, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

room_challenge = Table('association', Base.metadata,
    Column('Room', Integer, ForeignKey('Room.id')),
    Column('Challenge', Integer, ForeignKey('Challenge.id'))
)
class Room(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    gpsposition = db.Column(db.String(200))
    points = db.Column(db.Integer)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    challenges = relationship("Challenges", secondary=room_challenge, back_populates="rooms")
    def to_dict(self):
        data = {
            "id": self.id,
            "name": self.name,
            "gpsposition": self.gpsposition,
            "points": self.points,
            "timestamp": self.timestamp
            }
        return data

class Challenge(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    official = db.Column(db.Boolean, default=False)
    rooms = relationship("Rooms", secondary=room_challenge, back_populates="challenges")
    def to_dict(self):
        data = {
            "id": self.id,
            "name": self.name,
            "official": self.official,
            "rooms": [room.to_dict() for room in self.rooms],
        }
        return data

class User(db.Model):
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
