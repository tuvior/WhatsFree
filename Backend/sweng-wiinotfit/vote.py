import string
from google.appengine.ext import ndb

class Vote(ndb.Model):
    sub_id = ndb.StringProperty()
    user = ndb.StringProperty()
    value = ndb.IntegerProperty()