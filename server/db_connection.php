<?php
error_reporting(E_ALL);

// print array or dump var visually
function zarray($array)
{
  echo '<pre>';
  if (is_array($array))
  {
    print_r($array);
  }
  else
  {
    var_dump($array);
  }
  echo '</pre>';
}

// ==============================================================================================
// class for database connection
// (c) Jannis Mell 2011
class db_connection
{
  // Connection Settings
  private static $host = '';
  private static $database = '';
  private static $user = '';
  private static $password = '';
  private static $port = 3306;
  private $lastQuery = '';

  private $connection;
  private $error = false;
  private $errormessage = '';

  // constructor: try to connect to database - settings must have been specified before (via setupConnection)
  function __construct()
  {
    // Abort if no Connection settings specified
    if (db_connection::$host == '' OR db_connection::$database == '' OR db_connection::$user == '') return false;

    // try to connect
    $this->connection = @new mysqli(db_connection::$host,db_connection::$user,db_connection::$password,db_connection::$database, db_connection::$port);

    // set error if any
    if (mysqli_connect_errno() > 0) $this->setError('Error '.mysqli_connect_errno().': '.mysqli_connect_error());
    
    // tell error if any
    echo $this->getError();
    $this->connection->query("set names 'utf8';");
  }

  // Close connection on exit
  function __destruct()
  {
    if(!$this->error) mysqli_close($this->connection);
  }
  
  public function getVersion()
  {
   return $this->connection->server_info;
   }
   
   public function getLastQuery()
   {
    return $this->lastQuery;
   }
  
  // Set Connection Settings
  static function setupConnection($host,$db,$user,$pw, $port = 3306)
  {
    self::$host = $host;
    self::$database = $db;
    self::$user = $user;
    self::$password = $pw;
    self::$port = $port;
  }
  
  // Returns errormessage or false
  public function getError()
  {
    $tmperr = $this->errormessage;
    $this->errormessage = '';
    return ($this->error) ? $tmperr : false;
  }
  
  // sets errorstatus and errormessage
  private function setError($text)
  {
    $this->error = true;
    if ($this->errormessage == '')
    {
      $this->errormessage = $text;
    }
    else
    {
      $this->errormessage .= '<br />'.$text;
    }
  }
  
  // perform general query
  public function query($query)
  {
    if ($this->error) return false;
    $this->lastQuery = $query;
    if ($result = $this->connection->query($query)) {
      return $result;
    } else
    {
      $this->setError('Error: '.mysqli_error($this->connection));
      return false;
    }
  }
  
  // read from database
  public function read($table, $spalten = '*', $where = 0, $order = 0, $limit = 0, $useor = false, $output = false)
  {
    /*
      $table      Tabelle
      $spalten    * f&uuml;r alle oder String-Array
      $where      0 = ohne Bedingung, oder Array Key->Value
      $order      Sortierung nach Feld (zus&auml;tzlich ASC oder DESC m&ouml;glich)
      $limit      Begrenzung der Anzahl an Ergebnissen
      $output     Query mit Ausgeben  Array[0] = result, Array[1] = Query
    */

    if ($this->error) return false;
    
    $connector = ($useor) ? 'OR' : 'AND';
    $querystring = 'SELECT ';
    if ($spalten == '*')
    {
      $querystring .= '*';
    }
    elseif (is_array($spalten))
    {
    	foreach ($spalten as $a)
    	{
    		$querystring .= '`'.$a.'`,';
    	}
    	$querystring = substr($querystring,0,-1);
    }
    else
    {
      $this->setError('Error: No columns specified: '.$spalten);
      return false;
    }

    $querystring .= ' FROM `'.$table.'`';

    if($where !== 0)
    {
      if (is_array($where))
      {
        if(count($where) > 0)
        {
          $querystring .= ' WHERE ';
          foreach ($where as $col => $value)
          {
            if(strpos($value,'%') !== false) {
              $querystring .= 'LOWER(`'.$col.'`) LIKE \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';
            }
            elseif ($col[0] == '!') {
            	$querystring .= '`'.substr($col,1).'` != \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';	
            }
            else
            {
              $querystring .= '`'.$col.'` = \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';
            }
          }
          if ($useor)
          {
          $querystring = substr($querystring,0,-4);
          }
			else
{
	$querystring = substr($querystring,0,-5);
	}
        }
        else
        {
          $this->setError('Error: Wrong limitation format.');
          return false;
        }
      }
      else
      {
        $this->setError('Error: Wrong limitation format.');
        return false;
      }
    }
    
    if ($order !== 0)
	    $querystring .= ' ORDER BY '.$order;

    if (intval($limit) != 0)
    	$querystring .= ' LIMIT '.intval($limit);
    $this->lastQuery = $querystring;
    if ($result = $this->connection->query($querystring))
    {
      return ($output) ? array($result,$querystring) : $result;
    }
    else
    {
      $this->setError('Error: '.mysqli_error($this->connection).' (QUERY: '.$querystring.')');
      return false;
    }
  }
  
  public function replace($table, $values, $condition = false)
  {
   return $this->write($table, $values, $condition, 0, false, true);
  }

  // Insert data or update row(s)
  public function write($table, $values, $condition = false, $limit = 0, $output = false, $replace = false)
  {
    /*
    $table      Tabelle
    $values     Array Key->Value Eingabewerte
    $condition  false = neuer eintrag, Array Key->Value Bedingung = update
    $limit      Limit
    */
    if ($this->error) return false;
    if(!is_array($values))
    {
      $this->setError('Error: wrong insert data.');
      return false;
    }
    if (is_array($condition) && $replace == false)
    {
      $querystring = 'UPDATE `'.$table.'` SET ';
      foreach ($values as $key => $value)
      {
        $querystring .= '`'.$key.'` = \''.$this->connection->real_escape_string($value).'\',';
      }
      $querystring = substr($querystring,0,-1);
      $querystring .= ' WHERE `'.key($condition).'` = \''.$this->connection->real_escape_string($condition[key($condition)]).'\'';
      if($limit !== 0) $querystring .= ' LIMIT '.$limit;
    }
    elseif ($condition === false || $replace !== false)
    {
     $ins = ($replace) ? 'REPLACE' : 'INSERT';
      $querystring = $ins.' INTO `'.$table.'` (';
      foreach ($values as $key => $value)
      {
        $querystring .= '`'.$key.'`,';
      }
      $querystring = substr($querystring,0,-1);
      $querystring .= ') VALUES (';
      foreach ($values as $key => $value)
      {
        $querystring .= '\''.$this->connection->real_escape_string($value).'\',';
      }
      $querystring = substr($querystring,0,-1);
      $querystring .= ')';
    }
    else
    {
      $this->setError('Error: wrong condition');
      return false;
    }

    $querystring .= ';';
    $this->lastQuery = $querystring;
    if ($result = $this->connection->query($querystring))
    {
      if($condition === false)
      {
        $lastid = $this->connection->insert_id;
        return ($output) ? array($lastid,$querystring) : $lastid;
      } else
      {
        return ($output) ? $querystring : true;
      }
    }
    else
    {
      $this->setError('Error: '.mysqli_error($this->connection));
      return false;
    }
  }
  
  // count rows that match condition
  public function count($table, $group = '', $condition = false, $report = false)
  {
    if ($this->error) return false;
    
    $sql = 'SELECT COUNT(*) FROM `'.$table.'`';

    if (is_array($condition))
      {
        if(count($condition) > 0)
        {
          $sql .= ' WHERE ';
          foreach ($condition as $col => $value)
          {
            if(strpos($value,'%') !== false) {
              $sql .= '`'.$col.'` LIKE \''.$this->connection->real_escape_string($value).'\' AND ';
            }
            elseif ($col[0] == '!') {

            	$sql .= '`'.substr($col,1).'` != \''.$this->connection->real_escape_string($value).'\' AND ';	
            	}
            	else
            {
              $sql .= '`'.$col.'` = \''.$this->connection->real_escape_string($value).'\' AND ';
            }
          }
          $sql = substr($sql,0,-5);
        }
        else
        {
          $this->setError('Error: Wrong limitation format.');
          return false;
        }
      }
      if ($report) echo $sql;
      $this->lastQuery = $sql;
    $res = $this->connection->query($sql);
    $res = $res->fetch_object();
    return $res->{"COUNT(*)"};
  }
  
  // get enum values of column
	public function getEnumValues($table, $column)
  {
		if ($this->error) return false;
		$sql = 'SHOW COLUMNS FROM '.$table.' LIKE \''.$column.'\';';
      $this->lastQuery = $sql;
			$enum = $this->query($sql);
      if ($enum !== false)
    {
    			$enum = $enum->fetch_object();
			preg_match_all("/'([\w ]*)'/", $enum->Type, $values);
			$this->values = $values[1];
			return $this->values;

		} else {
		  $this->setError('Fehler aufgetreten');
			return false;
		}
	}
  
  // Delete rows
  public function delete($table, $condition, $useor = false)
  {
  /*
    $table      Tabelle
    $condition  Array Key->Value Bedingung
    $useor		true when OR should be used, AND used otherwise
    */
    if ($this->error) return false;
    $querystring = 'DELETE FROM `'.$table.'` ';
    $connector = ($useor) ? 'OR' : 'AND';

    if (is_array($condition))
    {
       if(count($condition) > 0)
        {
          $querystring .= ' WHERE ';
          foreach ($condition as $col => $value)
          {

            if(strpos($value,'%') !== false) {
              $querystring .= '`'.$col.'` LIKE \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';
            }
            elseif ($col[0] == '!') {

            	$querystring .= '`'.substr($col,1).'` != \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';	
             }
            	else
            {
              $querystring .= '`'.$col.'` = \''.$this->connection->real_escape_string($value).'\' '.$connector.' ';
            }
          }
           if ($useor)
          {
          	$querystring = substr($querystring,0,-4);
          }
			else
		{
			$querystring = substr($querystring,0,-5);
		}
      }
    } else
    {
      return false;
    }
    $this->lastQuery = $querystring;
    return $this->connection->query($querystring);
  }
  
  // Returns count of affected rows of recent query
  public function getAffectedRows()
  {
    if ($this->error) return false;
    return mysqli_affected_rows($this->connection);
  }
}
// end class db_connection
// ============================================================================================

// Generate random salt at given length
function generate_salt($length = 20){
  // Purposely left off quotation marks so it doesn't mess with SQL
  $chars =  'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.
            '0123456789``-=~!@#$%^&*()_+,./<>?;:[]{}\|';

  $str = '';
  $max = strlen($chars) - 1;

  for ($i=0; $i < $length; $i++)
    $str .= $chars[rand(0, $max)];

  return $str;
}

// Very complex hash function (GNU)
// Written by: http://juliusbeckmann.de/
// This function is using only sha1() and strrev() which are
// available on every PHP5 installation.
function hash_password($p, $s, $iter=5) {
  // ALWAYS return a multiple hashed pass salt combination
  $hash = sha1(sha1($p.$s).sha1(strrev($p).strrev($s)));

  // Rehashing the hash will make cracking process much slower
  for($i=0;$i<=$iter;++$i)
    $hash = sha1(sha1($hash).sha1(strrev($hash)));

  return $hash;
}

// ======================================

function getQ($ending, $carray, $cid = 0)
      {
      	
			$q = '';      	
      	foreach ($carray as $c)
      	{
      		$s = '';
      		if (($cid == 0 && in_array($ending, explode(' ', $c->suffixes))) OR $c->id == $cid)
      		{
      			$s = ' selected';
      		} 
          $q .= '<option value="'.$c->id.'"'.$s.'>'.$c->name.'</option>';
         }
			return $q;
      }
      
      
function find_links($text)
{
if ($text[0] == '#') return substr($text,1);
$str = preg_replace(
  '`([^"=\'>])(((http|https|ftp)://|www.)[^\s<]+[^\s<\.)])`i',
  '$1<a href="$2" target="_window">$2</a>',  $text);
return $str;

}

function isValidEmail($email){
    return preg_match("^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$^", $email);
}
