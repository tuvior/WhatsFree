import webapp2
from google.appengine.ext import ndb

def json_response( status ):
    if status == 0:
        res = """{
    "register": {
        "status": "ok"
    }
}"""
    elif status == 1:
        res = """{
    "register": {
        "status": "failure",
        "reason": "email"
    }
}"""
    elif status == 2:
        res = """{
    "register": {
        "status": "failure",
        "reason": "username"
    }
}"""
    elif status == -1:
        res = """{
    "register": {
        "status": "invalid"
    }
}"""
    return res

class User(ndb.Model):
    username = ndb.StringProperty()
    pswd = ndb.StringProperty()
    email = ndb.StringProperty()

class Register(webapp2.RequestHandler):            
    
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        email = self.request.get("email")
       
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
         
        if not username or not password or not email:
            self.response.write(json_response(-1))
        else:
            query_user = User.query(User.username == username).fetch()
            query_email = User.query(User.email == email).fetch()
            if query_user:
                self.response.write(json_response(2))
            elif query_email:
                self.response.write(json_response(1))
            else: 
                user = User(username = username, pswd = password, email = email)
                db_result = user.put()
                if not db_result:
                    self.response.write(json_response(0))
                else:
                    """ database error, what do? """
                    self.response.write('something went wrong')

app = webapp2.WSGIApplication([
    ('/register', Register),
], debug=True)