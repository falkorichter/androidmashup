<?php

/**
 * Application form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseApplicationForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_application' => new sfWidgetFormInputHidden(),
      'package'        => new sfWidgetFormInput(),
      'name'           => new sfWidgetFormInput(),
      'url'            => new sfWidgetFormInput(),
      'icon'           => new sfWidgetFormInput(),
      'apk_url'        => new sfWidgetFormInput(),
      'developer_id'   => new sfWidgetFormPropelChoice(array('model' => 'Developer', 'add_empty' => false)),
    ));

    $this->setValidators(array(
      'id_application' => new sfValidatorPropelChoice(array('model' => 'Application', 'column' => 'id_application', 'required' => false)),
      'package'        => new sfValidatorString(array('max_length' => 45)),
      'name'           => new sfValidatorString(array('max_length' => 45)),
      'url'            => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'icon'           => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'apk_url'        => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'developer_id'   => new sfValidatorPropelChoice(array('model' => 'Developer', 'column' => 'id_developer')),
    ));

    $this->widgetSchema->setNameFormat('application[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Application';
  }


}
