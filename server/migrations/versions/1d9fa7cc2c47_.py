"""empty message

Revision ID: 1d9fa7cc2c47
Revises: 
Create Date: 2019-06-04 17:16:25.528601

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '1d9fa7cc2c47'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('Challenge',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=64), nullable=True),
    sa.Column('official', sa.Boolean(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_Challenge_name'), 'Challenge', ['name'], unique=True)
    op.create_table('Room',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=64), nullable=True),
    sa.Column('gpsposition', sa.String(length=200), nullable=True),
    sa.Column('points', sa.Integer(), nullable=True),
    sa.Column('timestamp', sa.DateTime(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_Room_name'), 'Room', ['name'], unique=True)
    op.create_table('User',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=64), nullable=True),
    sa.Column('points', sa.Integer(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_User_name'), 'User', ['name'], unique=True)
    op.create_table('room_challenge',
    sa.Column('Room_id', sa.Integer(), nullable=True),
    sa.Column('Challenge_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['Challenge_id'], ['Challenge.id'], ),
    sa.ForeignKeyConstraint(['Room_id'], ['Room.id'], )
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('room_challenge')
    op.drop_index(op.f('ix_User_name'), table_name='User')
    op.drop_table('User')
    op.drop_index(op.f('ix_Room_name'), table_name='Room')
    op.drop_table('Room')
    op.drop_index(op.f('ix_Challenge_name'), table_name='Challenge')
    op.drop_table('Challenge')
    # ### end Alembic commands ###
