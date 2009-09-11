<?php

/**
 * Developer form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseDeveloperForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_developer' => new sfWidgetFormInputHidden(),
      'username'     => new sfWidgetFormInput(),
      'url'          => new sfWidgetFormInput(),
      'email'        => new sfWidgetFormInput(),
    ));

    $this->setValidators(array(
      'id_developer' => new sfValidatorPropelChoice(array('model' => 'Developer', 'column' => 'id_developer', 'required' => false)),
      'username'     => new sfValidatorString(array('max_length' => 45)),
      'url'          => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'email'        => new sfValidatorString(array('max_length' => 45)),
    ));

    $this->widgetSchema->setNameFormat('developer[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Developer';
  }


}
